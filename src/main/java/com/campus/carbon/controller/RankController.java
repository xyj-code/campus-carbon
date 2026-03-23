package com.campus.carbon.controller;

import com.campus.carbon.service.impl.RankServiceImpl;
import com.campus.carbon.model.dto.RankResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rank")
public class RankController {

    @Autowired
    private RankServiceImpl rankService;

    @GetMapping("/data")
    public RankResponse getRankData(
            @RequestParam String studentId,
            @RequestParam String timeRange) {
        return rankService.getRankData(studentId, timeRange);
    }
}
