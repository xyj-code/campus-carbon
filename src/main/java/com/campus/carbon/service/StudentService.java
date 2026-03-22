package com.campus.carbon.service;

import com.campus.carbon.model.Student;

public interface StudentService {
    Student login(String username, String password);
}
