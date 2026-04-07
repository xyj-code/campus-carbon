package com.campus.carbon.controller;

import com.campus.carbon.mapper.ProjectMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.mapper.UserProjectMapper;
import com.campus.carbon.model.Project;
import com.campus.carbon.model.Student;
import com.campus.carbon.model.dto.CertificateVO;
import com.campus.carbon.model.dto.Result;
import com.campus.carbon.model.dto.UserProjectVO;
import com.campus.carbon.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
public class CarbonProjectController {

    @Autowired private ProjectMapper projectMapper;
    @Autowired private UserProjectMapper userProjectMapper;
    @Autowired private StudentMapper studentMapper;
    @Autowired private PointsService pointsService;

    @GetMapping("/carbon-total")
    public Result<Map<String, Object>> getUserTotalCarbon(@RequestParam String username) {
        return Result.success(pointsService.getProfile(username));
    }

    @GetMapping("/my-projects")
    public Result<List<UserProjectVO>> getUserProjectList(@RequestParam String username) {
        return Result.success(userProjectMapper.selectByUsername(username));
    }

    @PostMapping("/check-unlock")
    public Result<List<UserProjectVO>> checkAndUnlock(@RequestParam String username) {
        Map<String, Object> profile = pointsService.getProfile(username);
        double totalCarbon = ((Number) profile.getOrDefault("totalCarbon", 0.0)).doubleValue();
        for (Project p : projectMapper.selectAll()) {
            if (p.getRequiredCarbon() == null) continue;
            if (totalCarbon >= p.getRequiredCarbon()
                    && userProjectMapper.countByUsernameAndProjectId(username, p.getId()) == 0) {
                userProjectMapper.insertByUsername(username, p.getId(), buildCode(p));
            }
        }
        return Result.success(userProjectMapper.selectByUsername(username));
    }

    @GetMapping("/detail/{id}")
    public Result<Project> getProjectDetail(@PathVariable Integer id) {
        Project p = projectMapper.selectById(id);
        return p != null ? Result.success(p) : Result.error(404, "项目不存在");
    }

    @GetMapping("/certificate")
    public Result<CertificateVO> getCertificate(@RequestParam String username,
                                                 @RequestParam Integer userProjectId) {
        UserProjectVO up = userProjectMapper.selectByUsernameAndId(username, userProjectId);
        if (up == null) return Result.error(404, "未找到认养记录");
        Student student = studentMapper.selectByUsername(username);
        Map<String, Object> profile = pointsService.getProfile(username);
        double carbon = ((Number) profile.getOrDefault("totalCarbon", 0.0)).doubleValue();

        CertificateVO cert = new CertificateVO();
        cert.setUserName(student != null && student.getName() != null ? student.getName() : username);
        cert.setProjectName(up.getProjectName());
        cert.setUniqueCode(up.getUniqueCode());
        cert.setUnlockTime(up.getUnlockTime() != null ? up.getUnlockTime().toString() : "");
        cert.setCertificateUrl(String.valueOf(carbon));
        return Result.success(cert);
    }

    @GetMapping("/all")
    public Result<List<Project>> getAllProjects() {
        return Result.success(projectMapper.selectAll());
    }

    private String buildCode(Project p) {
        String prefix = (p.getLocation() != null && p.getLocation().length() >= 2)
                ? p.getLocation().substring(0, 2) : "LC";
        return prefix + "-" + LocalDate.now().toString().replace("-", "")
                + "-" + String.format("%05d", (int)(Math.random() * 100000));
    }
}
