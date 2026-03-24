package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.ExchangeRecordMapper;
import com.campus.carbon.mapper.PointsRecordMapper;
import com.campus.carbon.mapper.ProductMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ExchangeRecordMapper exchangeRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    public Map<String, Object> getProductList(int page, int size) {
        int offset = (page - 1) * size;
        List<Product> list = productMapper.selectAll(offset, size);
        int total = productMapper.countAll();
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("currentPage", page);
        result.put("totalPages", (int) Math.ceil((double) total / size));
        return result;
    }

    @Transactional
    public Map<String, Object> exchange(String username, Long productId) {
        Map<String, Object> result = new HashMap<>();

        Product product = productMapper.selectById(productId);
        if (product == null) {
            result.put("code", 404);
            result.put("message", "商品不存在");
            return result;
        }

        Student student = studentMapper.selectByUsername(username);
        int currentPoints = (student != null && student.getPoints() != null) ? student.getPoints() : 0;
        if (currentPoints < product.getPoint()) {
            result.put("code", 400);
            result.put("message", "积分不足，还差 " + (product.getPoint() - currentPoints) + " 积分");
            return result;
        }

        // 原子扣库存，0行受影响说明已售罄
        int affected = productMapper.decreaseStock(productId);
        if (affected == 0) {
            result.put("code", 400);
            result.put("message", "商品库存不足");
            return result;
        }

        // 兑换记录
        ExchangeRecord er = new ExchangeRecord();
        er.setUsername(username);
        er.setProductId(productId);
        er.setProductName(product.getName());
        er.setPoint(product.getPoint());
        exchangeRecordMapper.insert(er);

        // 积分消耗记录
        PointsRecord pr = new PointsRecord();
        pr.setUsername(username);
        pr.setType("expense");
        pr.setAmount(product.getPoint());
        pr.setTitle("积分兑换");
        pr.setRemark("兑换 " + product.getName());
        pointsRecordMapper.insert(pr);

        // 扣减积分（GREATEST(0, points-n) 防止负数）
        studentMapper.addPoints(username, -product.getPoint());

        result.put("code", 200);
        result.put("message", "兑换成功！");
        result.put("remainPoints", currentPoints - product.getPoint());
        return result;
    }

    public List<ExchangeRecord> getExchangeRecords(String username) {
        return exchangeRecordMapper.selectByUsername(username);
    }
}
