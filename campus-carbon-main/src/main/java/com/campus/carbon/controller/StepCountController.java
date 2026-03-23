package com.campus.carbon.controller;

import com.campus.carbon.model.StepCount;
import com.campus.carbon.service.StepCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/step")
public class StepCountController {

    @Autowired
    private StepCountService stepCountService;

    @GetMapping("/count")
    public StepCount getStepCount(@RequestParam String studentId,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        StepCount stepCount = stepCountService.getStepCountByStudentIdAndDate(studentId, date);
        if (stepCount == null) {
            // 如果没有数据，返回默认值
            stepCount = new StepCount();
            stepCount.setStudentId(studentId);
            stepCount.setDate(date);
            stepCount.setSteps(0);
            stepCount.setCalories(0.0);
            stepCount.setDuration(0);
        }
        return stepCount;
    }

    @GetMapping("/list")
    public List<StepCount> getStepCountList(@RequestParam String studentId) {
        return stepCountService.getStepCountsByStudentId(studentId);
    }

    @PostMapping("/save")
    public int saveStepCount(@RequestBody StepCount stepCount) {
        return stepCountService.saveStepCount(stepCount);
    }
}
