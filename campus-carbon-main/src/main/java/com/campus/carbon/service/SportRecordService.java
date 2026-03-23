package com.campus.carbon.service;

import com.campus.carbon.model.SportRecord;

import java.util.List;

public interface SportRecordService {
    // 待实现：获取学生的运动记录列表
    List<SportRecord> getSportRecordsByStudentId(String studentId);
    
    // 待实现：添加运动记录
    int saveSportRecord(SportRecord sportRecord);
}