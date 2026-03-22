package com.campus.carbon.controller;

import com.campus.carbon.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    /** 个人主页数据：累计减碳、积分、连续打卡天数 */
    @GetMapping("/profile")
    public Map<String, Object> getProfile(@RequestParam String username) {
        return pointsService.getProfile(username);
    }

    /** 积分明细（分页+类型过滤） */
    @GetMapping("/records")
    public Map<String, Object> getRecords(
            @RequestParam String username,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return pointsService.getRecords(username, type, page, size);
    }
}
