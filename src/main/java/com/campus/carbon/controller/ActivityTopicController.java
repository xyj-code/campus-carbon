package com.campus.carbon.controller;

import com.campus.carbon.service.ActivityTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityTopicController {

    @Autowired
    private ActivityTopicService activityTopicService;

    @GetMapping("/hub")
    public Map<String, Object> getActivityHub(@RequestParam String username,
                                              @RequestParam(required = false) String activityCode) {
        return activityTopicService.getActivityHub(username, activityCode);
    }
}
