package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.ExchangeRecordMapper;
import com.campus.carbon.mapper.PointsRecordMapper;
import com.campus.carbon.mapper.SportRecordMapper;
import com.campus.carbon.mapper.StepCountMapper;
import com.campus.carbon.mapper.StudentMapper;
import com.campus.carbon.mapper.TaskTemplateMapper;
import com.campus.carbon.mapper.UserTaskMapper;
import com.campus.carbon.model.ExchangeRecord;
import com.campus.carbon.model.PointsRecord;
import com.campus.carbon.model.SportRecord;
import com.campus.carbon.model.StepCount;
import com.campus.carbon.model.TaskTemplate;
import com.campus.carbon.model.UserTask;
import com.campus.carbon.model.dto.TaskBoardItemVO;
import com.campus.carbon.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String PERIOD_DAILY = "DAILY";
    private static final String PERIOD_WEEKLY = "WEEKLY";
    private static final String TITLE_TASK_REWARD = "TASK_REWARD";
    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat("0.##");

    @Autowired
    private TaskTemplateMapper taskTemplateMapper;

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Autowired
    private StepCountMapper stepCountMapper;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private ExchangeRecordMapper exchangeRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Override
    @Transactional
    public void syncUserTasks(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }

        List<TaskTemplate> templates = taskTemplateMapper.selectActive();
        if (templates == null || templates.isEmpty()) {
            return;
        }

        List<StepCount> stepCounts = stepCountMapper.selectByStudentId(username);
        List<SportRecord> sportRecords = sportRecordMapper.selectByStudentId(username);
        List<ExchangeRecord> exchangeRecords = exchangeRecordMapper.selectAllByUsername(username);

        LocalDate today = LocalDate.now(CHINA_ZONE);
        PeriodWindow dailyWindow = buildDailyWindow(today);
        PeriodWindow weeklyWindow = buildWeeklyWindow(today);

        for (TaskTemplate template : templates) {
            PeriodWindow window = PERIOD_WEEKLY.equals(template.getPeriodType()) ? weeklyWindow : dailyWindow;
            double computedValue = calculateMetric(template.getMetricType(), window, stepCounts, sportRecords, exchangeRecords);

            UserTask userTask = userTaskMapper.selectOne(username, template.getTaskCode(), window.periodKey);
            if (userTask == null) {
                userTask = new UserTask();
                userTask.setUsername(username);
                userTask.setTaskCode(template.getTaskCode());
                userTask.setPeriodType(template.getPeriodType());
                userTask.setPeriodKey(window.periodKey);
                userTask.setCurrentValue(0D);
                userTask.setTargetValue(defaultNumber(template.getTargetValue()));
                userTask.setRewardPoints(defaultInt(template.getRewardPoints()));
                userTask.setCompleted(0);
                userTask.setRewardStatus(0);
                userTaskMapper.insert(userTask);
            }

            double nextValue = Math.max(defaultNumber(userTask.getCurrentValue()), computedValue);
            userTask.setCurrentValue(nextValue);
            userTask.setTargetValue(defaultNumber(template.getTargetValue()));
            userTask.setRewardPoints(defaultInt(template.getRewardPoints()));

            if (userTask.getCompleted() == null || userTask.getCompleted() == 0) {
                if (nextValue >= defaultNumber(template.getTargetValue())) {
                    userTask.setCompleted(1);
                    userTask.setCompletedTime(new Date());
                }
            }

            if ((userTask.getCompleted() != null && userTask.getCompleted() == 1)
                    && (userTask.getRewardStatus() == null || userTask.getRewardStatus() == 0)
                    && defaultInt(template.getRewardPoints()) > 0) {
                awardTaskReward(username, template, window.periodKey);
                userTask.setRewardStatus(1);
                userTask.setRewardTime(new Date());
            }

            userTaskMapper.update(userTask);
        }
    }

    @Override
    public Map<String, Object> getTaskBoard(String username) {
        syncUserTasks(username);

        List<TaskTemplate> templates = taskTemplateMapper.selectActive();
        LocalDate today = LocalDate.now(CHINA_ZONE);
        PeriodWindow dailyWindow = buildDailyWindow(today);
        PeriodWindow weeklyWindow = buildWeeklyWindow(today);

        List<TaskBoardItemVO> dailyTasks = new ArrayList<>();
        List<TaskBoardItemVO> weeklyTasks = new ArrayList<>();

        int dailyCompleted = 0;
        int weeklyCompleted = 0;

        for (TaskTemplate template : templates) {
            PeriodWindow window = PERIOD_WEEKLY.equals(template.getPeriodType()) ? weeklyWindow : dailyWindow;
            UserTask userTask = userTaskMapper.selectOne(username, template.getTaskCode(), window.periodKey);
            TaskBoardItemVO item = toBoardItem(template, userTask, window);
            if (PERIOD_WEEKLY.equals(template.getPeriodType())) {
                weeklyTasks.add(item);
                if (Boolean.TRUE.equals(item.getCompleted())) {
                    weeklyCompleted++;
                }
            } else {
                dailyTasks.add(item);
                if (Boolean.TRUE.equals(item.getCompleted())) {
                    dailyCompleted++;
                }
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("dailyCompleted", dailyCompleted);
        summary.put("dailyTotal", dailyTasks.size());
        summary.put("weeklyCompleted", weeklyCompleted);
        summary.put("weeklyTotal", weeklyTasks.size());
        summary.put("dailyPeriodLabel", dailyWindow.label);
        summary.put("weeklyPeriodLabel", weeklyWindow.label);

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("dailyTasks", dailyTasks);
        result.put("weeklyTasks", weeklyTasks);
        return result;
    }

    private void awardTaskReward(String username, TaskTemplate template, String periodKey) {
        PointsRecord record = new PointsRecord();
        record.setUsername(username);
        record.setType("income");
        record.setAmount(defaultInt(template.getRewardPoints()));
        record.setTitle(TITLE_TASK_REWARD);
        record.setRemark("task=" + template.getTaskCode() + " period=" + periodKey);
        pointsRecordMapper.insert(record);
        studentMapper.addPoints(username, defaultInt(template.getRewardPoints()));
    }

    private TaskBoardItemVO toBoardItem(TaskTemplate template, UserTask userTask, PeriodWindow window) {
        TaskBoardMeta meta = resolveMeta(template.getTaskCode());
        double currentValue = userTask != null ? defaultNumber(userTask.getCurrentValue()) : 0D;
        double targetValue = defaultNumber(template.getTargetValue());
        int percent = targetValue <= 0 ? 0 : (int) Math.min(Math.round((currentValue / targetValue) * 100), 100);
        boolean completed = userTask != null && userTask.getCompleted() != null && userTask.getCompleted() == 1;
        boolean rewarded = userTask != null && userTask.getRewardStatus() != null && userTask.getRewardStatus() == 1;

        TaskBoardItemVO item = new TaskBoardItemVO();
        item.setTaskCode(template.getTaskCode());
        item.setTitle(meta.title);
        item.setSubtitle(meta.subtitle);
        item.setPeriodType(template.getPeriodType());
        item.setPeriodLabel(window.label);
        item.setIcon(meta.icon);
        item.setAccent(meta.accent);
        item.setCurrentValue(currentValue);
        item.setTargetValue(targetValue);
        item.setRewardPoints(defaultInt(template.getRewardPoints()));
        item.setProgressPercent(percent);
        item.setProgressText(formatProgress(template.getTaskCode(), currentValue, targetValue));
        item.setStatusText(resolveStatusText(completed, rewarded));
        item.setCompleted(completed);
        item.setRewarded(rewarded);
        return item;
    }

    private String resolveStatusText(boolean completed, boolean rewarded) {
        if (completed && rewarded) {
            return "\u5df2\u81ea\u52a8\u53d1\u653e";
        }
        if (completed) {
            return "\u5df2\u5b8c\u6210";
        }
        return "\u8fdb\u884c\u4e2d";
    }

    private String formatProgress(String taskCode, double currentValue, double targetValue) {
        if ("DAILY_STEP_6000".equals(taskCode) || "WEEKLY_STEP_50000".equals(taskCode)) {
            return formatInt(currentValue) + " / " + formatInt(targetValue) + "\u6b65";
        }
        if ("DAILY_SPORT_2KM".equals(taskCode)) {
            return VALUE_FORMAT.format(currentValue) + " / " + VALUE_FORMAT.format(targetValue) + "km";
        }
        if ("WEEKLY_SPORT_3_TIMES".equals(taskCode) || "WEEKLY_REDEEM_1".equals(taskCode) || "DAILY_CHECKIN_1".equals(taskCode)) {
            return formatInt(currentValue) + " / " + formatInt(targetValue) + "\u6b21";
        }
        return VALUE_FORMAT.format(currentValue) + " / " + VALUE_FORMAT.format(targetValue);
    }

    private TaskBoardMeta resolveMeta(String taskCode) {
        if ("DAILY_STEP_6000".equals(taskCode)) {
            return new TaskBoardMeta(
                    "\u4eca\u65e5\u6b65\u884c 6000 \u6b65",
                    "\u5b8c\u6210\u4eca\u65e5\u7eff\u8272\u51fa\u884c\u76ee\u6807\uff0c\u81ea\u52a8\u589e\u52a0\u4efb\u52a1\u79ef\u5206",
                    "\uD83D\uDC5F",
                    "#55b879"
            );
        }
        if ("DAILY_SPORT_2KM".equals(taskCode)) {
            return new TaskBoardMeta(
                    "\u4eca\u65e5\u8fd0\u52a8 2km",
                    "\u8bb0\u5f55\u4e00\u6b21\u8fd0\u52a8\u8ddd\u79bb\uff0c\u5b8c\u6210\u5f53\u65e5\u6d3b\u529b\u6307\u6807",
                    "\uD83C\uDFC3",
                    "#4fa8bb"
            );
        }
        if ("DAILY_CHECKIN_1".equals(taskCode)) {
            return new TaskBoardMeta(
                    "\u5b8c\u6210\u4e00\u6b21\u4f4e\u78b3\u6253\u5361",
                    "\u5f53\u65e5\u53ea\u8981\u6709\u6b65\u6570\u6216\u8fd0\u52a8\u8bb0\u5f55\uff0c\u5373\u89c6\u4e3a\u5b8c\u6210\u6253\u5361",
                    "\uD83C\uDF31",
                    "#3e9c69"
            );
        }
        if ("WEEKLY_STEP_50000".equals(taskCode)) {
            return new TaskBoardMeta(
                    "\u672c\u5468\u7d2f\u8ba1 50000 \u6b65",
                    "\u5c06\u6bcf\u65e5\u51fa\u884c\u79ef\u7d2f\u6210\u5468\u6311\u6218\uff0c\u5f62\u6210\u7a33\u5b9a\u7684\u4f4e\u78b3\u8282\u594f",
                    "\uD83D\uDEB6",
                    "#2f8c58"
            );
        }
        if ("WEEKLY_SPORT_3_TIMES".equals(taskCode)) {
            return new TaskBoardMeta(
                    "\u672c\u5468\u8fd0\u52a8 3 \u6b21",
                    "\u4ee5\u8fd0\u52a8\u9891\u6b21\u5f62\u6210\u6bcf\u5468\u575a\u6301\u673a\u5236\uff0c\u5f3a\u5316\u4f4e\u78b3\u884c\u4e3a\u4e60\u60ef",
                    "\u26A1",
                    "#5b9cff"
            );
        }
        if ("WEEKLY_REDEEM_1".equals(taskCode)) {
            return new TaskBoardMeta(
                    "\u672c\u5468\u5151\u6362 1 \u6b21\u7eff\u8272\u6743\u76ca",
                    "\u628a\u79ef\u5206\u884c\u4e3a\u8f6c\u5316\u4e3a\u771f\u5b9e\u6821\u56ed\u6743\u76ca\uff0c\u5f62\u6210\u6fc0\u52b1\u95ed\u73af",
                    "\uD83C\uDF81",
                    "#f39c3b"
            );
        }
        return new TaskBoardMeta(
                "\u4efb\u52a1\u76ee\u6807",
                "\u5b8c\u6210\u4efb\u52a1\u540e\u53ef\u81ea\u52a8\u83b7\u5f97\u989d\u5916\u79ef\u5206",
                "\u2728",
                "#63bd81"
        );
    }

    private double calculateMetric(String metricType, PeriodWindow window, List<StepCount> stepCounts,
                                   List<SportRecord> sportRecords, List<ExchangeRecord> exchangeRecords) {
        if ("STEP_TOTAL".equals(metricType)) {
            return stepCounts.stream()
                    .filter(item -> inDateRange(item.getDate(), window.startDate, window.endDate))
                    .mapToDouble(item -> item.getSteps() != null ? item.getSteps() : 0D)
                    .sum();
        }
        if ("SPORT_DISTANCE_TOTAL".equals(metricType)) {
            return sportRecords.stream()
                    .filter(item -> inDateTimeRange(item.getStartTime(), window.startDateTime, window.endDateTime))
                    .mapToDouble(item -> item.getAmount() != null ? item.getAmount() : 0D)
                    .sum();
        }
        if ("ACTIVE_DAY_COUNT".equals(metricType)) {
            boolean hasStep = stepCounts.stream()
                    .anyMatch(item -> inDateRange(item.getDate(), window.startDate, window.endDate)
                            && item.getSteps() != null && item.getSteps() > 0);
            boolean hasSport = sportRecords.stream()
                    .anyMatch(item -> inDateTimeRange(item.getStartTime(), window.startDateTime, window.endDateTime)
                            && item.getAmount() != null && item.getAmount() > 0);
            return (hasStep || hasSport) ? 1D : 0D;
        }
        if ("SPORT_RECORD_COUNT".equals(metricType)) {
            return sportRecords.stream()
                    .filter(item -> inDateTimeRange(item.getStartTime(), window.startDateTime, window.endDateTime))
                    .count();
        }
        if ("BENEFIT_REDEEM_COUNT".equals(metricType)) {
            return exchangeRecords.stream()
                    .filter(item -> inDateTimeRange(item.getCreateTime(), window.startDateTime, window.endDateTime))
                    .count();
        }
        return 0D;
    }

    private boolean inDateRange(Date source, LocalDate startDate, LocalDate endDate) {
        if (source == null) {
            return false;
        }
        LocalDate value = Instant.ofEpochMilli(source.getTime()).atZone(CHINA_ZONE).toLocalDate();
        return !value.isBefore(startDate) && !value.isAfter(endDate);
    }

    private boolean inDateTimeRange(Date source, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (source == null) {
            return false;
        }
        LocalDateTime value = Instant.ofEpochMilli(source.getTime()).atZone(CHINA_ZONE).toLocalDateTime();
        return !value.isBefore(startDateTime) && !value.isAfter(endDateTime);
    }

    private PeriodWindow buildDailyWindow(LocalDate date) {
        String label = date.format(DAY_FORMATTER) + " \u4eca\u65e5\u4efb\u52a1";
        return new PeriodWindow(
                PERIOD_DAILY,
                date.toString(),
                date,
                date,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX),
                label
        );
    }

    private PeriodWindow buildWeeklyWindow(LocalDate date) {
        LocalDate start = date.with(DayOfWeek.MONDAY);
        LocalDate end = date.with(DayOfWeek.SUNDAY);
        int week = date.get(WeekFields.of(Locale.CHINA).weekOfWeekBasedYear());
        String label = start.format(DAY_FORMATTER) + " - " + end.format(DAY_FORMATTER);
        return new PeriodWindow(
                PERIOD_WEEKLY,
                date.getYear() + "-W" + String.format("%02d", week),
                start,
                end,
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX),
                label
        );
    }

    private double defaultNumber(Double value) {
        return value == null ? 0D : value;
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String formatInt(double value) {
        return String.valueOf((int) Math.round(value));
    }

    private static class PeriodWindow {
        private final String periodType;
        private final String periodKey;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;
        private final String label;

        private PeriodWindow(String periodType, String periodKey, LocalDate startDate, LocalDate endDate,
                             LocalDateTime startDateTime, LocalDateTime endDateTime, String label) {
            this.periodType = periodType;
            this.periodKey = periodKey;
            this.startDate = startDate;
            this.endDate = endDate;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.label = label;
        }
    }

    private static class TaskBoardMeta {
        private final String title;
        private final String subtitle;
        private final String icon;
        private final String accent;

        private TaskBoardMeta(String title, String subtitle, String icon, String accent) {
            this.title = title;
            this.subtitle = subtitle;
            this.icon = icon;
            this.accent = accent;
        }
    }
}
