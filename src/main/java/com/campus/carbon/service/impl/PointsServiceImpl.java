package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.PointsRecordMapper;
import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.model.PointsRecord;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.model.Student;
import com.campus.carbon.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StepCountMapper stepCountMapper;

    private static final double STEP_TO_CARBON = 0.005 / 100; // kg/步

    @Override
    public void awardStepPoints(String username, int deltaSteps) {
        if (deltaSteps <= 0) return;
        double carbonKg = deltaSteps * STEP_TO_CARBON;
        int pts = (int) Math.round(carbonKg * 100);
        if (pts <= 0) return;

        PointsRecord record = new PointsRecord();
        record.setUsername(username);
        record.setType("income");
        record.setAmount(pts);
        record.setTitle("步数减碳");
        record.setRemark(String.format("步数+%d步，减碳%.1fg", deltaSteps, carbonKg * 1000));
        pointsRecordMapper.insert(record);
        studentMapper.addPoints(username, pts);
    }

    @Override
    public Map<String, Object> getProfile(String username) {
        Map<String, Object> result = new HashMap<>();
        Student student = studentMapper.selectByUsername(username);
        if (student == null) {
            result.put("code", 404);
            return result;
        }

        // 累计减碳量（从步数记录汇总）
        List<StepCount> allSteps = stepCountMapper.selectByStudentId(username);
        int totalSteps = allSteps.stream()
                .mapToInt(s -> s.getSteps() != null ? s.getSteps() : 0)
                .sum();
        double totalCarbon = Math.round(totalSteps * STEP_TO_CARBON * 100) / 100.0;

        // 连续打卡天数（从今天往前数）
        Set<LocalDate> dates = allSteps.stream()
                .filter(s -> s.getDate() != null)
                .map(s -> s.getDate().toInstant()
                        .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                        .toLocalDate())
                .collect(Collectors.toSet());
        int consecutiveDays = 0;
        LocalDate check = LocalDate.now(java.time.ZoneId.of("Asia/Shanghai"));
        while (dates.contains(check)) {
            consecutiveDays++;
            check = check.minusDays(1);
        }

        result.put("username", student.getUsername());
        result.put("name", student.getName());
        result.put("points", student.getPoints() != null ? student.getPoints() : 0);
        result.put("totalCarbon", totalCarbon);
        result.put("totalSteps", totalSteps);
        result.put("consecutiveDays", consecutiveDays);
        return result;
    }

    @Override
    public Map<String, Object> getRecords(String username, String type, int page, int size) {
        int offset = (page - 1) * size;
        List<PointsRecord> list;
        int total;

        if ("all".equals(type)) {
            list = pointsRecordMapper.selectByUsername(username, offset, size);
            total = pointsRecordMapper.countByUsername(username);
        } else {
            list = pointsRecordMapper.selectByUsernameAndType(username, type, offset, size);
            total = pointsRecordMapper.countByUsernameAndType(username, type);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("currentPage", page);
        result.put("totalPages", (int) Math.ceil((double) total / size));
        return result;
    }
}
