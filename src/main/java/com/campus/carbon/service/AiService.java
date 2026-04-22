package com.campus.carbon.service;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.model.dto.AgentActionRequest;
import com.campus.carbon.model.dto.AgentBriefVO;

import java.util.List;

public interface AiService {
    AiSuggest getCarbonSuggestion(String carbonFootprint);

    AiSuggest getHealthSuggestion(List<HealthData> healthDataList);

    AgentBriefVO getAgentBrief(String userId, String userNote);

    AgentBriefVO startAgentAction(AgentActionRequest request);

    AgentBriefVO completeAgentAction(AgentActionRequest request);

    AgentBriefVO skipAgentAction(AgentActionRequest request);
}
