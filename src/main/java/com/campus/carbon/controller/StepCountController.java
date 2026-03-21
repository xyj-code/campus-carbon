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
        return stepCountService.getStepCountByStudentIdAndDate(studentId, date);
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
