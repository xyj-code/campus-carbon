package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.ExchangeRecordMapper;
import com.campus.carbon.mapper.PointsRecordMapper;
import com.campus.carbon.mapper.ProductMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.model.ExchangeRecord;
import com.campus.carbon.model.PointsRecord;
import com.campus.carbon.model.Product;
import com.campus.carbon.model.Student;
import com.campus.carbon.model.dto.BenefitItemVO;
import com.campus.carbon.model.dto.BenefitRecordVO;
import com.campus.carbon.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl {

    private static final String STATUS_PENDING = "PENDING_CLAIM";
    private static final String STATUS_READY = "READY_TO_VIEW";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ExchangeRecordMapper exchangeRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private TaskService taskService;

    public Map<String, Object> getProductList(int page, int size) {
        int offset = (page - 1) * size;
        List<Product> list = productMapper.selectAll(offset, size);
        int total = productMapper.countAll();

        List<BenefitItemVO> rights = new ArrayList<>();
        for (Product product : list) {
            rights.add(toBenefitItem(product));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", rights);
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
            result.put("message", "RIGHT_NOT_FOUND");
            return result;
        }

        BenefitMeta meta = resolveMeta(product.getProductCode(), product.getCategoryCode());

        Student student = studentMapper.selectByUsername(username);
        int currentPoints = (student != null && student.getPoints() != null) ? student.getPoints() : 0;
        if (currentPoints < product.getPoint()) {
            result.put("code", 400);
            result.put("message", "POINTS_NOT_ENOUGH");
            result.put("shortfall", product.getPoint() - currentPoints);
            return result;
        }

        int affected = productMapper.decreaseStock(productId);
        if (affected == 0) {
            result.put("code", 400);
            result.put("message", "RIGHT_SOLD_OUT");
            return result;
        }

        ExchangeRecord exchangeRecord = new ExchangeRecord();
        exchangeRecord.setUsername(username);
        exchangeRecord.setProductId(productId);
        exchangeRecord.setProductCode(product.getProductCode());
        exchangeRecord.setCategoryCode(product.getCategoryCode());
        exchangeRecord.setPoint(product.getPoint());
        exchangeRecord.setStatus(meta.recordStatus);
        exchangeRecordMapper.insert(exchangeRecord);

        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setUsername(username);
        pointsRecord.setType("expense");
        pointsRecord.setAmount(product.getPoint());
        pointsRecord.setTitle("BENEFIT_REDEEM");
        pointsRecord.setRemark("redeem=" + product.getCategoryCode() + ":" + product.getProductCode());
        pointsRecordMapper.insert(pointsRecord);

        studentMapper.addPoints(username, -product.getPoint());
        taskService.syncUserTasks(username);

        result.put("code", 200);
        result.put("message", "REDEEM_SUCCESS");
        result.put("remainPoints", currentPoints - product.getPoint());
        result.put("benefitName", meta.name);
        result.put("claimMessage", meta.exchangeHint);
        result.put("productCode", product.getProductCode());
        result.put("categoryCode", product.getCategoryCode());
        return result;
    }

    public List<BenefitRecordVO> getExchangeRecords(String username) {
        List<ExchangeRecord> records = exchangeRecordMapper.selectByUsername(username);
        List<BenefitRecordVO> result = new ArrayList<>();
        for (ExchangeRecord record : records) {
            result.add(toBenefitRecord(record));
        }
        return result;
    }

    private BenefitItemVO toBenefitItem(Product product) {
        BenefitMeta meta = resolveMeta(product.getProductCode(), product.getCategoryCode());

        BenefitItemVO item = new BenefitItemVO();
        item.setId(product.getId());
        item.setName(meta.name);
        item.setSubtitle(meta.subtitle);
        item.setPoint(product.getPoint());
        item.setStock(product.getStock());
        item.setCategory(meta.category);
        item.setCategoryLabel(meta.categoryLabel);
        item.setIcon(meta.icon);
        item.setImage(meta.image);
        item.setSponsor(meta.sponsor);
        item.setDeliveryMode(meta.deliveryMode);
        item.setDescription(meta.description);
        item.setExchangeHint(meta.exchangeHint);
        item.setAvailable(product.getStock() != null && product.getStock() > 0);
        return item;
    }

    private BenefitRecordVO toBenefitRecord(ExchangeRecord record) {
        BenefitMeta meta = resolveMeta(record.getProductCode(), record.getCategoryCode());

        BenefitRecordVO vo = new BenefitRecordVO();
        vo.setId(record.getId());
        vo.setProductId(record.getProductId());
        vo.setProductCode(record.getProductCode());
        vo.setName(meta.name);
        vo.setSubtitle(meta.subtitle);
        vo.setCategory(meta.category);
        vo.setCategoryLabel(meta.categoryLabel);
        vo.setIcon(meta.icon);
        vo.setImage(meta.image);
        vo.setPoint(record.getPoint());
        vo.setSponsor(meta.sponsor);
        vo.setStatus(resolveStatusLabel(record.getStatus()));
        vo.setNextStep(meta.exchangeHint);
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }

    private String resolveStatusLabel(String status) {
        if (STATUS_READY.equals(status)) {
            return "\u5df2\u751f\u6210";
        }
        return "\u5f85\u6838\u9500";
    }

    private BenefitMeta resolveMeta(String productCode, String categoryCode) {
        if ("DINING_MEAL_COUPON".equals(productCode)) {
            return new BenefitMeta(
                    "\u98df\u5802\u8f7b\u98df\u5957\u9910\u5238",
                    "\u7eff\u8272\u7a97\u53e3\u53ef\u76f4\u63a5\u4f7f\u7528",
                    "DINING",
                    "\u9910\u996e\u6743\u76ca",
                    "\uD83C\uDF5D",
                    "/static/mall/dining-meal-coupon.png",
                    "\u540e\u52e4\u670d\u52a1\u4e2d\u5fc3",
                    "\u5230\u5e97\u6838\u9500",
                    "\u628a\u4f4e\u78b3\u79ef\u5206\u53d8\u6210\u53ef\u611f\u77e5\u7684\u6821\u56ed\u9910\u996e\u56de\u62a5\uff0c\u5f15\u5bfc\u66f4\u5065\u5eb7\u7684\u5c31\u9910\u9009\u62e9\u3002",
                    "\u5151\u6362\u540e\u51ed\u8bb0\u5f55\u9875\u9762\u5230\u7eff\u8272\u9910\u7a97\u53e3\u6838\u9500",
                    STATUS_PENDING
            );
        }
        if ("DINING_DRINK_COUPON".equals(productCode)) {
            return new BenefitMeta(
                    "\u6821\u56ed\u996e\u54c1\u51cf\u514d\u5238",
                    "\u9002\u7528\u4e8e\u65e0\u7cd6\u996e\u54c1\u4e0e\u81ea\u5e26\u676f\u573a\u666f",
                    "DINING",
                    "\u9910\u996e\u6743\u76ca",
                    "\uD83E\uDD64",
                    "/static/mall/dining-drink-coupon.png",
                    "\u6821\u56ed\u996e\u54c1\u7ad9",
                    "\u7ebf\u4e0b\u6838\u9500",
                    "\u5c06\u4f4e\u78b3\u884c\u4e3a\u4e0e\u73af\u4fdd\u676f\u4f7f\u7528\u8054\u52a8\uff0c\u8ba9\u7ee7\u7eed\u4f4e\u78b3\u6709\u66f4\u76f4\u63a5\u7684\u6b63\u5411\u53cd\u9988\u3002",
                    "\u5151\u6362\u540e\u5230\u996e\u54c1\u7ad9\u51fa\u793a\u6838\u9500\u8bb0\u5f55\u5373\u53ef\u4f7f\u7528",
                    STATUS_PENDING
            );
        }
        if ("STATIONERY_PEN".equals(productCode)) {
            return new BenefitMeta(
                    "\u5706\u73e0\u7b14",
                    "\u65e5\u5e38\u8bb0\u5f55\u4e0e\u8bfe\u5802\u7b14\u8bb0\u53ef\u76f4\u63a5\u4f7f\u7528",
                    "STATIONERY",
                    "\u5b66\u4e60\u6743\u76ca",
                    "\uD83D\uDCDD",
                    "/static/mall/stationery-pen.png",
                    "\u6821\u56ed\u6587\u521b\u4e2d\u5fc3",
                    "\u7ebf\u4e0b\u9886\u53d6",
                    "\u5c06\u4f4e\u78b3\u79ef\u5206\u8f6c\u5316\u4e3a\u771f\u5b9e\u53ef\u7528\u7684\u5b66\u4e60\u6587\u5177\uff0c\u8ba9\u5b66\u4e60\u573a\u666f\u7684\u6fc0\u52b1\u66f4\u5177\u4f53\u3002",
                    "\u5151\u6362\u540e\u51ed\u8bb0\u5f55\u9875\u9762\u5230\u6587\u521b\u9886\u53d6\u70b9\u9886\u53d6",
                    STATUS_PENDING
            );
        }
        if ("STATIONERY_PENCIL".equals(productCode)) {
            return new BenefitMeta(
                    "\u94c5\u7b14",
                    "\u9002\u5408\u624b\u7ed8\u3001\u8349\u7a3f\u4e0e\u8bfe\u5802\u4f7f\u7528",
                    "STATIONERY",
                    "\u5b66\u4e60\u6743\u76ca",
                    "\u270F\uFE0F",
                    "/static/mall/stationery-pencil.png",
                    "\u6821\u56ed\u6587\u521b\u4e2d\u5fc3",
                    "\u7ebf\u4e0b\u9886\u53d6",
                    "\u8ba9\u5b66\u751f\u7684\u4f4e\u78b3\u79ef\u5206\u80fd\u591f\u5151\u6362\u6210\u9ad8\u9891\u4f7f\u7528\u7684\u5b66\u4e60\u6587\u5177\uff0c\u63d0\u5347\u79ef\u5206\u7684\u771f\u5b9e\u611f\u3002",
                    "\u5151\u6362\u6210\u529f\u540e\u5230\u6307\u5b9a\u9886\u53d6\u70b9\u51fa\u793a\u8bb0\u5f55\u5373\u53ef",
                    STATUS_PENDING
            );
        }
        if ("CERT_GREEN_PIONEER".equals(productCode)) {
            return new BenefitMeta(
                    "\u7eff\u8272\u884c\u52a8\u5148\u950b\u8bc1\u4e66",
                    "\u7528\u4e8e\u5c55\u793a\u4e2a\u4eba\u4f4e\u78b3\u5b9e\u8df5\u6210\u679c",
                    "CERT",
                    "\u8363\u8a89\u6743\u76ca",
                    "\uD83C\uDFC5",
                    "/static/mall/cert-green-pioneer.png",
                    "\u56e2\u59d4",
                    "\u7535\u5b50\u8bc1\u4e66",
                    "\u4e3a\u7528\u6237\u751f\u6210\u53ef\u7528\u4e8e\u5c55\u793a\u7684\u7eff\u8272\u6210\u957f\u6210\u679c\uff0c\u8ba9\u79ef\u5206\u4e0d\u53ea\u662f\u6570\u5b57\uff0c\u8fd8\u662f\u4e2a\u4eba\u8363\u8a89\u3002",
                    "\u5151\u6362\u540e\u53ef\u5728\u4e2a\u4eba\u4e2d\u5fc3\u6216\u8bc1\u4e66\u9875\u9762\u67e5\u770b",
                    STATUS_READY
            );
        }
        if ("CERT_LOW_CARBON_STAR".equals(productCode)) {
            return new BenefitMeta(
                    "\u4f4e\u78b3\u8fbe\u4eba\u8363\u8a89\u5fbd\u7ae0",
                    "\u7528\u4e8e\u4e2a\u4eba\u4e3b\u9875\u5c55\u793a\u4e0e\u6d3b\u52a8\u7533\u62a5",
                    "CERT",
                    "\u8363\u8a89\u6743\u76ca",
                    "\uD83C\uDF1F",
                    "/static/mall/cert-low-carbon-star.png",
                    "\u5b66\u751f\u53d1\u5c55\u4e2d\u5fc3",
                    "\u7cfb\u7edf\u53d1\u653e",
                    "\u5c06\u79ef\u5206\u6210\u5c31\u8f6c\u5316\u4e3a\u53ef\u611f\u77e5\u7684\u7b26\u53f7\u5316\u8363\u8a89\uff0c\u8ba9\u7528\u6237\u7684\u4f4e\u78b3\u79ef\u7d2f\u6709\u66f4\u5f3a\u7684\u5c55\u793a\u4ef7\u503c\u3002",
                    "\u5151\u6362\u540e\u7cfb\u7edf\u5c06\u81ea\u52a8\u751f\u6210\u5fbd\u7ae0\u8bb0\u5f55",
                    STATUS_READY
            );
        }

        if ("CERT".equals(categoryCode)) {
            return new BenefitMeta(
                    "\u7eff\u8272\u8363\u8a89\u6743\u76ca",
                    "\u57fa\u4e8e\u5f53\u524d\u79ef\u5206\u751f\u6210\u7684\u6210\u957f\u6807\u8bc6",
                    "CERT",
                    "\u8363\u8a89\u6743\u76ca",
                    "\uD83C\uDFC6",
                    "/static/mall/cert-default.png",
                    "\u5b66\u751f\u53d1\u5c55\u4e2d\u5fc3",
                    "\u7cfb\u7edf\u53d1\u653e",
                    "\u7528\u4e8e\u8bb0\u5f55\u4e2a\u4eba\u4f4e\u78b3\u6210\u957f\u8db3\u8ff9\u3002",
                    "\u5151\u6362\u540e\u53ef\u5728\u4e2a\u4eba\u4e2d\u5fc3\u67e5\u770b",
                    STATUS_READY
            );
        }
        if ("STATIONERY".equals(categoryCode)) {
            return new BenefitMeta(
                    "\u5b66\u4e60\u6587\u5177",
                    "\u7528\u79ef\u5206\u5151\u6362\u65e5\u5e38\u53ef\u7528\u7684\u6587\u5177\u7528\u54c1",
                    "STATIONERY",
                    "\u5b66\u4e60\u6743\u76ca",
                    "\uD83D\uDCDD",
                    "/static/mall/stationery-default.png",
                    "\u6821\u56ed\u6587\u521b\u4e2d\u5fc3",
                    "\u7ebf\u4e0b\u6838\u9500",
                    "\u4fdd\u6301\u5b66\u4e60\u573a\u666f\u4e0e\u4f4e\u78b3\u6fc0\u52b1\u7684\u76f4\u63a5\u8054\u52a8\uff0c\u8ba9\u79ef\u5206\u6709\u771f\u5b9e\u7684\u4f7f\u7528\u573a\u666f\u3002",
                    "\u5151\u6362\u540e\u6309\u6307\u5f15\u9886\u53d6",
                    STATUS_PENDING
            );
        }

        return new BenefitMeta(
                "\u7eff\u8272\u9910\u996e\u6743\u76ca",
                "\u7528\u79ef\u5206\u5151\u6362\u53ef\u611f\u77e5\u7684\u6821\u56ed\u9910\u996e\u56de\u62a5",
                "DINING",
                "\u9910\u996e\u6743\u76ca",
                "\uD83C\uDF74",
                "/static/mall/dining-default.png",
                "\u540e\u52e4\u670d\u52a1\u4e2d\u5fc3",
                "\u5230\u5e97\u6838\u9500",
                "\u8ba9\u4f4e\u78b3\u79ef\u5206\u4e0e\u65e5\u5e38\u996e\u98df\u4f53\u9a8c\u5efa\u7acb\u66f4\u5f3a\u7684\u8fde\u63a5\u3002",
                "\u5151\u6362\u540e\u6309\u6307\u5f15\u6838\u9500",
                STATUS_PENDING
        );
    }

    private static class BenefitMeta {
        private final String name;
        private final String subtitle;
        private final String category;
        private final String categoryLabel;
        private final String icon;
        private final String image;
        private final String sponsor;
        private final String deliveryMode;
        private final String description;
        private final String exchangeHint;
        private final String recordStatus;

        private BenefitMeta(String name,
                            String subtitle,
                            String category,
                            String categoryLabel,
                            String icon,
                            String image,
                            String sponsor,
                            String deliveryMode,
                            String description,
                            String exchangeHint,
                            String recordStatus) {
            this.name = name;
            this.subtitle = subtitle;
            this.category = category;
            this.categoryLabel = categoryLabel;
            this.icon = icon;
            this.image = image;
            this.sponsor = sponsor;
            this.deliveryMode = deliveryMode;
            this.description = description;
            this.exchangeHint = exchangeHint;
            this.recordStatus = recordStatus;
        }
    }
}
