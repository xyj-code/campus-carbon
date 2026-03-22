package com.campus.carbon.controller;

import com.campus.carbon.model.Student;
import com.campus.carbon.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = new HashMap<>();
        Student student = studentService.login(username, password);
        if (student != null) {
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("username", student.getUsername());
            result.put("name", student.getName());
        } else {
            result.put("code", 401);
            result.put("message", "账号或密码错误");
        }
        return result;
    }
}
