package com.campus.carbon.service;

import java.util.Map;

public interface ActivityTopicService {
    Map<String, Object> getActivityHub(String username);
    Map<String, Object> getActivityHub(String username, String activityCode);
}
