package com.campus.carbon.controller;

import com.campus.carbon.service.AdminTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/tasks")
public class AdminTaskController {

    @Autowired
    private AdminTaskService adminTaskService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return adminTaskService.getDashboard();
    }

    @PostMapping("/save")
    public Map<String, Object> saveTemplate(@RequestBody Map<String, Object> payload) {
        return adminTaskService.saveTemplate(payload);
    }

    @PostMapping("/{id}/toggle")
    public Map<String, Object> toggleTemplate(@PathVariable Long id) {
        return adminTaskService.toggleTemplate(id);
    }
}
