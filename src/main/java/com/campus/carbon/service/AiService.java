package com.campus.carbon.service;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;

import java.util.List;

public interface AiService {
    AiSuggest getCarbonSuggestion(String carbonFootprint);

    /**
     * 基于用户健康数据生成AI建议
     * @param healthDataList 用户健康数据列表
     * @return AI建议
     */
    AiSuggest getHealthSuggestion(List<HealthData> healthDataList);
}