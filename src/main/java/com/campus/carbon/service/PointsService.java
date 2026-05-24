package com.campus.carbon.service;

import java.util.Map;

public interface PointsService {
    void awardStepPoints(String username, int deltaSteps);
    void awardSportPoints(String username, double distanceKm);
    Map<String, Object> getProfile(String username);
    Map<String, Object> getRecords(String username, String type, int page, int size);
}
