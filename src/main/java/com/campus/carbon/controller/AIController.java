package com.campus.carbon.controller;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.model.dto.AgentBriefVO;
import com.campus.carbon.model.dto.AgentPlanRequest;
import com.campus.carbon.service.AiService;
import com.campus.carbon.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/health-suggest")
    public AiSuggest getHealthSuggestion(@RequestParam String userId) {
        List<HealthData> healthDataList = healthDataService.getByUserId(userId);
        return aiService.getHealthSuggestion(healthDataList);
    }

    @GetMapping("/agent-brief")
    public AgentBriefVO getAgentBrief(@RequestParam String userId) {
        return aiService.getAgentBrief(userId, "");
    }

    @PostMapping("/agent-plan")
    public AgentBriefVO getAgentPlan(@RequestBody AgentPlanRequest request) {
        String userId = request == null ? "" : request.getUserId();
        String userNote = request == null ? "" : request.getUserNote();
        return aiService.getAgentBrief(userId, userNote);
    }
}
