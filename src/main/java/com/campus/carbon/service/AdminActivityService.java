package com.campus.carbon.service;

import java.util.Map;

public interface AdminActivityService {
    Map<String, Object> getDashboard();
    Map<String, Object> saveActivity(Map<String, Object> payload);
    Map<String, Object> deleteActivity(Long id);
    Map<String, Object> toggleActivity(Long id);
}
