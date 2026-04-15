package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.PointsRecordMapper;
import com.campus.carbon.mapper.SportRecordMapper;
import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.model.PointsRecord;
import com.campus.carbon.model.SportRecord;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.model.Student;
import com.campus.carbon.service.PointsService;
import com.campus.carbon.util.CarbonMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PointsServiceImpl implements PointsService {

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StepCountMapper stepCountMapper;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Override
    public void awardStepPoints(String username, int deltaSteps) {
        if (deltaSteps <= 0) {
            return;
        }

        double carbonKg = CarbonMetrics.carbonFromSteps(deltaSteps);
        int points = CarbonMetrics.pointsFromCarbon(carbonKg);
        if (points <= 0) {
            return;
        }

        PointsRecord record = new PointsRecord();
        record.setUsername(username);
        record.setType("income");
        record.setAmount(points);
        record.setTitle("STEP_CARBON");
        record.setRemark(String.format("steps=%d carbon=%.3fkg", deltaSteps, carbonKg));
        pointsRecordMapper.insert(record);
        studentMapper.addPoints(username, points);
    }

    @Override
    public void awardSportPoints(String username, double distanceKm) {
        if (distanceKm <= 0) {
            return;
        }

        double carbonKg = CarbonMetrics.carbonFromSportDistance(distanceKm);
        int points = CarbonMetrics.pointsFromCarbon(carbonKg);
        if (points <= 0) {
            return;
        }

        PointsRecord record = new PointsRecord();
        record.setUsername(username);
        record.setType("income");
        record.setAmount(points);
        record.setTitle("SPORT_CARBON");
        record.setRemark(String.format("distance=%.2fkm carbon=%.3fkg", distanceKm, carbonKg));
        pointsRecordMapper.insert(record);
        studentMapper.addPoints(username, points);
    }

    @Override
    public Map<String, Object> getProfile(String username) {
        Map<String, Object> result = new HashMap<>();
        Student student = studentMapper.selectByUsername(username);
        if (student == null) {
            result.put("code", 404);
            return result;
        }

        List<StepCount> allSteps = stepCountMapper.selectByStudentId(username);
        List<SportRecord> allSports = sportRecordMapper.selectByStudentId(username);

        int totalSteps = allSteps.stream()
                .mapToInt(item -> item.getSteps() != null ? item.getSteps() : 0)
                .sum();
        double totalSportDistance = allSports.stream()
                .mapToDouble(item -> item.getAmount() != null ? item.getAmount() : 0D)
                .sum();

        double stepCarbon = allSteps.stream()
                .mapToDouble(item -> CarbonMetrics.carbonFromSteps(item.getSteps() != null ? item.getSteps() : 0))
                .sum();
        double sportCarbon = allSports.stream()
                .mapToDouble(item -> CarbonMetrics.carbonFromSportDistance(item.getAmount() != null ? item.getAmount() : 0D))
                .sum();

        Set<LocalDate> activeDates = new HashSet<>();
        allSteps.stream()
                .filter(item -> item.getDate() != null)
                .map(item -> item.getDate().toInstant().atZone(CHINA_ZONE).toLocalDate())
                .forEach(activeDates::add);
        allSports.stream()
                .filter(item -> item.getStartTime() != null)
                .map(item -> item.getStartTime().toInstant().atZone(CHINA_ZONE).toLocalDate())
                .forEach(activeDates::add);

        int consecutiveDays = 0;
        LocalDate cursor = LocalDate.now(CHINA_ZONE);
        while (activeDates.contains(cursor)) {
            consecutiveDays++;
            cursor = cursor.minusDays(1);
        }

        result.put("username", student.getUsername());
        result.put("name", student.getName());
        result.put("points", student.getPoints() != null ? student.getPoints() : 0);
        result.put("totalCarbon", CarbonMetrics.roundCarbon(stepCarbon + sportCarbon));
        result.put("stepCarbon", CarbonMetrics.roundCarbon(stepCarbon));
        result.put("sportCarbon", CarbonMetrics.roundCarbon(sportCarbon));
        result.put("totalSteps", totalSteps);
        result.put("totalSportDistance", Math.round(totalSportDistance * 100) / 100.0);
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
