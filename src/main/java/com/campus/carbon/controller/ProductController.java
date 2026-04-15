package com.campus.carbon.controller;

import com.campus.carbon.model.dto.BenefitRecordVO;
import com.campus.carbon.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/list")
    public Map<String, Object> getProductList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return productService.getProductList(page, size);
    }

    @PostMapping("/exchange")
    public Map<String, Object> exchange(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        Long productId = Long.valueOf(params.get("productId").toString());
        return productService.exchange(username, productId);
    }

    @GetMapping("/exchange/records")
    public List<BenefitRecordVO> getExchangeRecords(@RequestParam String username) {
        return productService.getExchangeRecords(username);
    }
}
