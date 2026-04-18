package com.campus.carbon.service;

import java.util.Map;

public interface AdminTaskService {
    Map<String, Object> getDashboard();
    Map<String, Object> saveTemplate(Map<String, Object> payload);
    Map<String, Object> toggleTemplate(Long id);
}
