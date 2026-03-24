package com.campus.carbon.service;

import com.campus.carbon.model.StepCount;

import java.util.Date;
import java.util.List;

public interface StepCountService {
    // 待实现：根据学生ID和日期获取步数统计
    StepCount getStepCountByStudentIdAndDate(String studentId, Date date);
    
    // 待实现：获取学生的步数统计列表
    List<StepCount> getStepCountsByStudentId(String studentId);
    
    // 待实现：添加或更新步数统计
    int saveStepCount(StepCount stepCount);
}