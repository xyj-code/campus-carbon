package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.model.dto.RankResponse;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankServiceImpl {

    @Autowired
    private StepCountMapper stepCountMapper;

    @Autowired
    private StudentMapper studentMapper;

    // 与首页保持一致：100步 = 0.005kg CO₂，即每步 0.00005 kg
    private static final double STEP_TO_CARBON = 0.005 / 100;

    public RankResponse getRankData(String studentId, String timeRange) {
        RankResponse response = new RankResponse();
        RankResponse.RankData data = new RankResponse.RankData();

        LocalDate startDate = getStartDate(timeRange);
        LocalDate endDate = LocalDate.now();

        // 只取步数数据参与排名，与首页减碳量公式一致
        List<StepCount> allSteps = stepCountMapper.selectAll();
        Map<String, Integer> stepsByUser = aggregateSteps(allSteps, startDate, endDate);

        List<RankResponse.RankItem> rankList = buildRankList(stepsByUser, studentId);
        RankResponse.MyRank myRank = buildMyRank(rankList, studentId);

        data.setMyRank(myRank);
        data.setRankList(rankList);
        response.setData(data);
        return response;
    }

    private LocalDate getStartDate(String timeRange) {
        LocalDate now = LocalDate.now();
        switch (timeRange) {
            case "today": return now;
            case "week":  return now.minusDays(6); // 含今天共7天
            case "month": return LocalDate.of(now.getYear(), now.getMonthValue(), 1);
            default:      return now.minusDays(6);
        }
    }

    /** 按用户汇总指定日期范围内的步数 */
    private Map<String, Integer> aggregateSteps(List<StepCount> allSteps, LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> map = new HashMap<>();
        for (StepCount step : allSteps) {
            LocalDate d = step.getDate().toInstant()
                    .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                    .toLocalDate();
            if (!d.isBefore(startDate) && !d.isAfter(endDate)) {
                map.merge(step.getStudentId(), step.getSteps() == null ? 0 : step.getSteps(), Integer::sum);
            }
        }
        return map;
    }

    /** 根据步数构建排名列表（按减碳量降序） */
    private List<RankResponse.RankItem> buildRankList(Map<String, Integer> stepsByUser, String currentUserId) {
        List<RankResponse.RankItem> list = new ArrayList<>();

        List<Map.Entry<String, Integer>> sorted = stepsByUser.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        String[] avatars = {"👑", "🥈", "🥉"};

        for (int i = 0; i < sorted.size(); i++) {
            String uid = sorted.get(i).getKey();
            int steps = sorted.get(i).getValue();
            double carbon = Math.round(steps * STEP_TO_CARBON * 100) / 100.0; // kg，保留2位小数
            int points = (int) Math.round(carbon * 100);

            Student student = studentMapper.selectByUsername(uid);
            String displayName = (student != null && student.getName() != null) ? student.getName() : uid;

            RankResponse.RankItem item = new RankResponse.RankItem();
            item.setId(uid);
            item.setUsername(uid);
            item.setName(displayName);
            item.setAvatar(i < 3 ? avatars[i] : "👤");
            item.setCarbonReduction(carbon);
            item.setPoints(points);
            item.setLikes(0);
            item.setLiked(false);
            item.setSelf(uid.equals(currentUserId));
            list.add(item);
        }

        // 无任何数据时，至少显示当前用户（0数据）
        if (list.isEmpty()) {
            Student student = studentMapper.selectByUsername(currentUserId);
            String displayName = (student != null && student.getName() != null) ? student.getName() : currentUserId;
            RankResponse.RankItem item = new RankResponse.RankItem();
            item.setId(currentUserId);
            item.setUsername(currentUserId);
            item.setName(displayName);
            item.setAvatar("👤");
            item.setCarbonReduction(0);
            item.setPoints(0);
            item.setLikes(0);
            item.setLiked(false);
            item.setSelf(true);
            list.add(item);
        }

        return list;
    }

    /** 从排名列表中提取当前用户的排名信息 */
    private RankResponse.MyRank buildMyRank(List<RankResponse.RankItem> rankList, String studentId) {
        RankResponse.MyRank myRank = new RankResponse.MyRank();
        for (int i = 0; i < rankList.size(); i++) {
            if (rankList.get(i).getId().equals(studentId)) {
                myRank.setRank(i + 1);
                myRank.setCarbonReduction(rankList.get(i).getCarbonReduction());
                myRank.setPoints(rankList.get(i).getPoints());
                return myRank;
            }
        }
        // 当前用户没有步数记录
        myRank.setRank(rankList.size() + 1);
        myRank.setCarbonReduction(0);
        myRank.setPoints(0);
        return myRank;
    }
}
