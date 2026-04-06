package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.HealthDataMapper;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class HealthDataServiceImpl implements HealthDataService {

    @Autowired
    private HealthDataMapper healthDataMapper;

    @Override
    public List<HealthData> getByUserId(String userId) {
        return healthDataMapper.selectByUserId(userId);
    }

    @Override
    public HealthData getById(Long id) {
        return healthDataMapper.selectById(id);
    }

    @Override
    public HealthData saveOrUpdate(HealthData healthData) {
        // 计算BMI：体重(kg) / 身高(m)的平方
        if (healthData.getHeight() != null && healthData.getWeight() != null && healthData.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightInMeters = healthData.getHeight().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal bmi = healthData.getWeight()
                    .divide(heightInMeters.multiply(heightInMeters), 2, RoundingMode.HALF_UP);
            healthData.setBmi(bmi);
        }

        // 检查是否已存在该日期的记录
        HealthData existing = healthDataMapper.selectByUserIdAndDate(healthData.getUserId(), healthData.getRecordDate());
        if (existing != null) {
            // 更新已有记录
            healthData.setId(existing.getId());
            healthDataMapper.update(healthData);
            return healthDataMapper.selectById(existing.getId());
        } else {
            // 插入新记录
            healthDataMapper.insert(healthData);
            return healthDataMapper.selectById(healthData.getId());
        }
    }

    @Override
    public boolean delete(Long id) {
        return healthDataMapper.delete(id) > 0;
    }
}
