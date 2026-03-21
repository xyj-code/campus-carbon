package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.model.Student;
import com.campus.carbon.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Student login(String stuNo, String password) {
        return studentMapper.selectByStuNoAndPassword(stuNo, password);
    }
}
