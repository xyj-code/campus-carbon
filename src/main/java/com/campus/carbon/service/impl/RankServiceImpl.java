package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.SportRecordMapper;
import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.model.SportRecord;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.model.Student;
import com.campus.carbon.model.dto.RankResponse;
import com.campus.carbon.util.CarbonMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankServiceImpl {

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    @Autowired
    private StepCountMapper stepCountMapper;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    public RankResponse getRankData(String studentId, String timeRange) {
        RankResponse response = new RankResponse();
        RankResponse.RankData data = new RankResponse.RankData();

        LocalDate startDate = getStartDate(timeRange);
        LocalDate endDate = LocalDate.now(CHINA_ZONE);

        Map<String, Double> carbonByUser = aggregateCarbon(
                stepCountMapper.selectAll(),
                sportRecordMapper.selectAll(),
                startDate,
                endDate
        );

        List<RankResponse.RankItem> rankList = buildRankList(carbonByUser, studentId);
        RankResponse.MyRank myRank = buildMyRank(rankList, studentId);

        data.setMyRank(myRank);
        data.setRankList(rankList);
        response.setData(data);
        return response;
    }

    private LocalDate getStartDate(String timeRange) {
        LocalDate now = LocalDate.now(CHINA_ZONE);
        switch (timeRange) {
            case "today":
                return now;
            case "week":
                return now.minusDays(6);
            case "month":
                return LocalDate.of(now.getYear(), now.getMonthValue(), 1);
            case "all":
                return LocalDate.of(2000, 1, 1);
            default:
                return now.minusDays(6);
        }
    }

    private Map<String, Double> aggregateCarbon(List<StepCount> allSteps,
                                                List<SportRecord> allSports,
                                                LocalDate startDate,
                                                LocalDate endDate) {
        Map<String, Double> carbonByUser = new HashMap<>();

        for (StepCount step : allSteps) {
            if (step.getDate() == null) {
                continue;
            }
            LocalDate date = step.getDate().toInstant().atZone(CHINA_ZONE).toLocalDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                carbonByUser.merge(
                        step.getStudentId(),
                        CarbonMetrics.carbonFromSteps(step.getSteps() != null ? step.getSteps() : 0),
                        Double::sum
                );
            }
        }

        for (SportRecord sport : allSports) {
            if (sport.getStartTime() == null) {
                continue;
            }
            LocalDate date = sport.getStartTime().toInstant().atZone(CHINA_ZONE).toLocalDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                carbonByUser.merge(
                        sport.getStudentId(),
                        CarbonMetrics.carbonFromSportDistance(sport.getAmount() != null ? sport.getAmount() : 0D),
                        Double::sum
                );
            }
        }

        return carbonByUser;
    }

    private List<RankResponse.RankItem> buildRankList(Map<String, Double> carbonByUser, String currentUserId) {
        List<RankResponse.RankItem> list = new ArrayList<>();

        List<Map.Entry<String, Double>> sorted = carbonByUser.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        String[] avatars = {"T1", "T2", "T3"};

        for (int i = 0; i < sorted.size(); i++) {
            String uid = sorted.get(i).getKey();
            double carbon = CarbonMetrics.roundCarbon(sorted.get(i).getValue());
            int points = CarbonMetrics.pointsFromCarbon(carbon);

            Student student = studentMapper.selectByUsername(uid);
            String displayName = (student != null && student.getName() != null) ? student.getName() : uid;

            RankResponse.RankItem item = new RankResponse.RankItem();
            item.setId(uid);
            item.setUsername(uid);
            item.setName(displayName);
            item.setAvatar(i < 3 ? avatars[i] : "U");
            item.setCarbonReduction(carbon);
            item.setPoints(points);
            item.setLikes(0);
            item.setLiked(false);
            item.setSelf(uid.equals(currentUserId));
            list.add(item);
        }

        if (list.isEmpty()) {
            Student student = studentMapper.selectByUsername(currentUserId);
            String displayName = (student != null && student.getName() != null) ? student.getName() : currentUserId;

            RankResponse.RankItem item = new RankResponse.RankItem();
            item.setId(currentUserId);
            item.setUsername(currentUserId);
            item.setName(displayName);
            item.setAvatar("U");
            item.setCarbonReduction(0);
            item.setPoints(0);
            item.setLikes(0);
            item.setLiked(false);
            item.setSelf(true);
            list.add(item);
        }

        return list;
    }

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

        myRank.setRank(rankList.size() + 1);
        myRank.setCarbonReduction(0);
        myRank.setPoints(0);
        return myRank;
    }
}
