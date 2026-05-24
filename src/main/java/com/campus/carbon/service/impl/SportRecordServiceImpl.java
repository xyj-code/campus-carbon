package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.SportRecordMapper;
import com.campus.carbon.model.SportRecord;
import com.campus.carbon.service.PointsService;
import com.campus.carbon.service.SportRecordService;
import com.campus.carbon.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportRecordServiceImpl implements SportRecordService {

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private TaskService taskService;

    @Override
    public List<SportRecord> getSportRecordsByStudentId(String studentId) {
        return sportRecordMapper.selectByStudentId(studentId);
    }

    @Override
    public int saveSportRecord(SportRecord sportRecord) {
        int result = sportRecordMapper.insert(sportRecord);
        if (result > 0 && sportRecord.getAmount() != null && sportRecord.getAmount() > 0) {
            pointsService.awardSportPoints(sportRecord.getStudentId(), sportRecord.getAmount());
        }
        if (result > 0) {
            taskService.syncUserTasks(sportRecord.getStudentId());
        }
        return result;
    }
}
