package com.campus.carbon.controller;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.service.AiService;
import com.campus.carbon.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AiService aiService;

    @Autowired
    private HealthDataService healthDataService;

    @PostMapping("/carbon-suggest")
    public AiSuggest getCarbonSuggestion(@RequestBody AiSuggest aiSuggest) {
        return aiService.getCarbonSuggestion(aiSuggest.getCarbonFootprint());
    }

    /**
     * 基于用户健康数据获取AI建议
     * @param userId 用户ID
     * @return AI健康建议
     */
    @GetMapping("/health-suggest")
    public AiSuggest getHealthSuggestion(@RequestParam String userId) {
        List<HealthData> healthDataList = healthDataService.getByUserId(userId);
        return aiService.getHealthSuggestion(healthDataList);
    }
}