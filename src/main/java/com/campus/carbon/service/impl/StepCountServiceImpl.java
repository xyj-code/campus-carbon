package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.service.PointsService;
import com.campus.carbon.service.StepCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StepCountServiceImpl implements StepCountService {

    @Autowired
    private StepCountMapper stepCountMapper;

    @Autowired
    private PointsService pointsService;

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
        // 保存前先读取今日旧数据，算出步数增量
        StepCount old = stepCountMapper.selectByStudentIdAndDate(
                stepCount.getStudentId(), stepCount.getDate());
        int oldSteps = (old != null && old.getSteps() != null) ? old.getSteps() : 0;
        int newSteps = stepCount.getSteps() != null ? stepCount.getSteps() : 0;

        int result = stepCountMapper.insert(stepCount);

        // 只对新增步数发放积分（用户减少步数不扣分）
        int delta = newSteps - oldSteps;
        if (delta > 0) {
            pointsService.awardStepPoints(stepCount.getStudentId(), delta);
        }
        return result;
    }
}
