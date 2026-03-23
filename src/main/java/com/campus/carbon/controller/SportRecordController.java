package com.campus.carbon.controller;

import com.campus.carbon.model.SportRecord;
import com.campus.carbon.service.SportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sport")
public class SportRecordController {

    @Autowired
    private SportRecordService sportRecordService;

    @GetMapping("/record")
    public List<SportRecord> getSportRecord(@RequestParam String studentId) {
        return sportRecordService.getSportRecordsByStudentId(studentId);
    }

    @PostMapping("/save")
    public int saveSportRecord(@RequestBody SportRecord sportRecord) {
        return sportRecordService.saveSportRecord(sportRecord);
    }
}
