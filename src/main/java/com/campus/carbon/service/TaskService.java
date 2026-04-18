package com.campus.carbon.service;

import java.util.List;
import java.util.Map;

public interface TaskService {
    void syncUserTasks(String username);
    Map<String, Object> getTaskBoard(String username);
    Map<String, Object> getTaskBoard(String username, List<String> taskCodes);
}
