package com.campus.carbon.controller;

import com.campus.carbon.model.StudentSchedule;
import com.campus.carbon.model.dto.AgentScheduleVO;
import com.campus.carbon.service.StudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class StudentScheduleController {

    @Autowired
    private StudentScheduleService studentScheduleService;

    @GetMapping("/list")
    public List<StudentSchedule> list(@RequestParam String username) {
        return studentScheduleService.listByUsername(username);
    }

    @GetMapping("/today")
    public List<StudentSchedule> today(@RequestParam String username) {
        return studentScheduleService.listToday(username);
    }

    @GetMapping("/context")
    public AgentScheduleVO context(@RequestParam String username) {
        return studentScheduleService.getTodayContext(username);
    }

    @PostMapping("/save")
    public StudentSchedule save(@RequestBody StudentSchedule schedule) {
        return studentScheduleService.save(schedule);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id, @RequestParam String username) {
        return studentScheduleService.delete(id, username);
    }
}
