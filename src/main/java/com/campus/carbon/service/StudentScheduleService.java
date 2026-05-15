package com.campus.carbon.service;

import com.campus.carbon.model.StudentSchedule;
import com.campus.carbon.model.dto.AgentScheduleVO;

import java.util.List;

public interface StudentScheduleService {
    List<StudentSchedule> listByUsername(String username);

    List<StudentSchedule> listToday(String username);

    StudentSchedule save(StudentSchedule schedule);

    boolean delete(Long id, String username);

    AgentScheduleVO getTodayContext(String username);
}
