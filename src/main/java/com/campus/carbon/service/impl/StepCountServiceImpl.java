package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.service.StepCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StepCountServiceImpl implements StepCountService {

    @Autowired
    private StepCountMapper stepCountMapper;

    @Override
    public StepCount getStepCountByStudentIdAndDate(String studentId, Date date) {
        return stepCountMapper.selectByStudentIdAndDate(studentId, date);
    }

    @Override
    public List<StepCount> getStepCountsByStudentId(String studentId) {
        return stepCountMapper.selectByStudentId(studentId);
    }

    @Override
    public int saveStepCount(StepCount stepCount) {
        return stepCountMapper.insert(stepCount);
    }
}
