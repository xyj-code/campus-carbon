package com.campus.carbon.service;

import com.campus.carbon.model.dto.AgentSportPlanVO;

public interface MapVenueService {
    AgentSportPlanVO buildSportPlan(String userNote, Double latitude, Double longitude);
}
