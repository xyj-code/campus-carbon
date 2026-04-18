package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.PointsRecordMapper;
import com.campus.carbon.mapper.SportRecordMapper;
import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.mapper.UserTreeMapper;
import com.campus.carbon.model.PointsRecord;
import com.campus.carbon.model.SportRecord;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.model.Student;
import com.campus.carbon.model.UserTree;
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
    private static final int[] TREE_LEVEL_THRESHOLDS = {0, 80, 180, 340, 560, 820, 1120};
    private static final String[] TREE_STAGE_KEYS = {"seed", "sprout", "sapling", "flourishing", "canopy", "guardian"};
    private static final String[] TREE_STAGE_NAMES = {
            "\u5fae\u5149\u79cd\u5b50",
            "\u7834\u571f\u65b0\u82bd",
            "\u5411\u9633\u5e7c\u82d7",
            "\u9752\u7fe0\u5c0f\u6811",
            "\u7e41\u8302\u4e4b\u6811",
            "\u5b88\u62a4\u53e4\u6811"
    };

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StepCountMapper stepCountMapper;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private UserTreeMapper userTreeMapper;

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

        UserTree userTree = syncUserTree(
                username,
                CarbonMetrics.roundCarbon(stepCarbon + sportCarbon)
        );
        appendTreeProfile(result, userTree);
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

    private UserTree syncUserTree(String username, double totalCarbon) {
        UserTree userTree = userTreeMapper.selectByUsername(username);
        if (userTree == null) {
            userTree = new UserTree();
            userTree.setUsername(username);
            userTree.setTreeLevel(1);
            userTree.setTreeStage(TREE_STAGE_KEYS[0]);
            userTree.setTreeName(TREE_STAGE_NAMES[0]);
            userTree.setGrowthValue(0);
            userTree.setWaterValue(0);
            userTree.setLastTotalCarbon(0D);
            userTreeMapper.insert(userTree);
        }

        double safeTotalCarbon = Math.max(totalCarbon, 0D);
        double lastTotalCarbon = defaultDouble(userTree.getLastTotalCarbon());
        double deltaCarbon = Math.max(safeTotalCarbon - lastTotalCarbon, 0D);

        int nextGrowthValue = defaultInt(userTree.getGrowthValue()) + (int) Math.round(deltaCarbon * 15);
        int nextWaterValue = defaultInt(userTree.getWaterValue()) + (int) Math.round(deltaCarbon * 24);
        int nextLevel = resolveTreeLevel(nextGrowthValue);
        int stageIndex = Math.max(0, Math.min(nextLevel - 1, TREE_STAGE_KEYS.length - 1));

        userTree.setGrowthValue(nextGrowthValue);
        userTree.setWaterValue(nextWaterValue);
        userTree.setTreeLevel(nextLevel);
        userTree.setTreeStage(TREE_STAGE_KEYS[stageIndex]);
        userTree.setTreeName(TREE_STAGE_NAMES[stageIndex]);
        userTree.setLastTotalCarbon(safeTotalCarbon);
        userTreeMapper.updateByUsername(userTree);
        return userTree;
    }

    private void appendTreeProfile(Map<String, Object> result, UserTree userTree) {
        int growthValue = defaultInt(userTree.getGrowthValue());
        int level = defaultInt(userTree.getTreeLevel());
        int levelIndex = Math.max(0, Math.min(level - 1, TREE_LEVEL_THRESHOLDS.length - 1));
        int currentBase = TREE_LEVEL_THRESHOLDS[levelIndex];
        int nextBase = levelIndex + 1 < TREE_LEVEL_THRESHOLDS.length
                ? TREE_LEVEL_THRESHOLDS[levelIndex + 1]
                : currentBase;
        int currentLevelGrowth = Math.max(growthValue - currentBase, 0);
        int nextLevelGrowth = Math.max(nextBase - currentBase, 0);
        int remainGrowth = Math.max(nextBase - growthValue, 0);
        int progressPercent = nextLevelGrowth <= 0
                ? 100
                : (int) Math.min(Math.round((currentLevelGrowth * 100.0) / nextLevelGrowth), 100);

        result.put("treeLevel", level);
        result.put("treeStage", userTree.getTreeStage());
        result.put("treeName", userTree.getTreeName());
        result.put("treeGrowthValue", growthValue);
        result.put("treeWaterValue", defaultInt(userTree.getWaterValue()));
        result.put("treeCurrentLevelGrowth", currentLevelGrowth);
        result.put("treeNextLevelGrowth", nextLevelGrowth);
        result.put("treeProgressPercent", progressPercent);
        result.put("treeRemainGrowth", remainGrowth);
        result.put("treeHint", remainGrowth <= 0
                ? "\u5f53\u524d\u53e4\u6811\u5df2\u8fdb\u5165\u7a33\u5b9a\u6210\u957f\u9636\u6bb5"
                : "\u518d\u79ef\u7d2f " + remainGrowth + " \u70b9\u6210\u957f\u503c\uff0c\u6811\u51a0\u4f1a\u518d\u6b21\u8fdb\u5316");
    }

    private int resolveTreeLevel(int growthValue) {
        int resolvedLevel = 1;
        for (int i = 0; i < TREE_LEVEL_THRESHOLDS.length; i++) {
            if (growthValue >= TREE_LEVEL_THRESHOLDS[i]) {
                resolvedLevel = i + 1;
            }
        }
        return Math.min(resolvedLevel, TREE_STAGE_KEYS.length);
    }

    private double defaultDouble(Double value) {
        return value != null ? value : 0D;
    }

    private int defaultInt(Integer value) {
        return value != null ? value : 0;
    }
}
