package com.campus.carbon.controller;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.model.dto.AgentActionRequest;
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
    public AgentBriefVO getAgentBrief(@RequestParam String userId,
                                      @RequestParam(required = false) Double latitude,
                                      @RequestParam(required = false) Double longitude) {
        return aiService.getAgentBrief(userId, "", latitude, longitude);
    }

    @PostMapping("/agent-plan")
    public AgentBriefVO getAgentPlan(@RequestBody AgentPlanRequest request) {
        String userId = request == null ? "" : request.getUserId();
        String userNote = request == null ? "" : request.getUserNote();
        Double latitude = request == null ? null : request.getLatitude();
        Double longitude = request == null ? null : request.getLongitude();
        return aiService.getAgentBrief(userId, userNote, latitude, longitude);
    }

    @PostMapping("/agent-action/start")
    public AgentBriefVO startAgentAction(@RequestBody AgentActionRequest request) {
        return aiService.startAgentAction(request);
    }

    @PostMapping("/agent-action/complete")
    public AgentBriefVO completeAgentAction(@RequestBody AgentActionRequest request) {
        return aiService.completeAgentAction(request);
    }

    @PostMapping("/agent-action/skip")
    public AgentBriefVO skipAgentAction(@RequestBody AgentActionRequest request) {
        return aiService.skipAgentAction(request);
    }
}
