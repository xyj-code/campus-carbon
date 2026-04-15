package com.campus.carbon.service;

import java.util.Map;

public interface TaskService {
    void syncUserTasks(String username);
    Map<String, Object> getTaskBoard(String username);
}
