package com.campus.carbon.controller;

import com.campus.carbon.service.AdminActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/activities")
public class AdminActivityController {

    @Autowired
    private AdminActivityService adminActivityService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return adminActivityService.getDashboard();
    }

    @PostMapping("/save")
    public Map<String, Object> saveActivity(@RequestBody Map<String, Object> payload) {
        return adminActivityService.saveActivity(payload);
    }

    @PostMapping("/{id}/toggle")
    public Map<String, Object> toggleActivity(@PathVariable Long id) {
        return adminActivityService.toggleActivity(id);
    }

    @PostMapping("/{id}/delete")
    public Map<String, Object> deleteActivity(@PathVariable Long id) {
        return adminActivityService.deleteActivity(id);
    }
}
