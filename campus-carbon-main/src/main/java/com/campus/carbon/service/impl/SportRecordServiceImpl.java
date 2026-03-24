package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.SportRecordMapper;
import com.campus.carbon.model.SportRecord;
import com.campus.carbon.service.SportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportRecordServiceImpl implements SportRecordService {

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Override
    public List<SportRecord> getSportRecordsByStudentId(String studentId) {
        return sportRecordMapper.selectByStudentId(studentId);
    }

    @Override
    public int saveSportRecord(SportRecord sportRecord) {
        return sportRecordMapper.insert(sportRecord);
    }
}
