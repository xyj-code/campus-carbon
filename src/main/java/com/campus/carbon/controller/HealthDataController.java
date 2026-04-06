package com.campus.carbon.controller;

import com.campus.carbon.model.HealthData;
import com.campus.carbon.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/health")
public class HealthDataController {

    @Autowired
    private HealthDataService healthDataService;

    /**
     * 获取用户的所有健康数据记录
     */
    @GetMapping("/list")
    public List<HealthData> getHealthDataList(@RequestParam String userId) {
        return healthDataService.getByUserId(userId);
    }

    /**
     * 根据ID获取健康数据
     */
    @GetMapping("/{id}")
    public HealthData getHealthDataById(@PathVariable Long id) {
        return healthDataService.getById(id);
    }

    /**
     * 新增或更新健康数据
     */
    @PostMapping("/save")
    public HealthData saveHealthData(@RequestBody HealthData healthData) {
        return healthDataService.saveOrUpdate(healthData);
    }

    /**
     * 删除健康数据
     */
    @DeleteMapping("/{id}")
    public boolean deleteHealthData(@PathVariable Long id) {
        return healthDataService.delete(id);
    }
}
