package com.campus.carbon.service;

import com.campus.carbon.model.HealthData;
import java.util.List;

public interface HealthDataService {
    /**
     * 获取用户的所有健康数据记录
     */
    List<HealthData> getByUserId(String userId);

    /**
     * 根据ID获取健康数据
     */
    HealthData getById(Long id);

    /**
     * 新增或更新健康数据（同一天只保留一条记录）
     */
    HealthData saveOrUpdate(HealthData healthData);

    /**
     * 删除健康数据
     */
    boolean delete(Long id);
}
