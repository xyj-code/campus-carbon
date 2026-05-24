package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.ActivityTopicMapper;
import com.campus.carbon.model.ActivityTopic;
import com.campus.carbon.model.dto.TaskBoardItemVO;
import com.campus.carbon.service.ActivityTopicService;
import com.campus.carbon.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ActivityTopicServiceImpl implements ActivityTopicService {

    @Autowired
    private ActivityTopicMapper activityTopicMapper;

    @Autowired
    private TaskService taskService;

    @Override
    public Map<String, Object> getActivityHub(String username) {
        return getActivityHub(username, null);
    }

    @Override
    public Map<String, Object> getActivityHub(String username, String activityCode) {
        List<ActivityTopic> activeActivities = activityTopicMapper.selectActive(new Date());
        activeActivities.forEach(this::normalizeActivityVisuals);

        ActivityTopic selectedActivity = resolveSelectedActivity(activeActivities, activityCode);
        Map<String, Object> taskBoard = selectedActivity == null
                ? emptyTaskBoard()
                : taskService.getTaskBoard(username, parseTaskCodes(selectedActivity));
        Map<String, Object> activityProgress = buildActivityProgress(selectedActivity, taskBoard);

        Map<String, Object> result = new HashMap<>();
        result.put("featured", activeActivities.isEmpty() ? null : activeActivities.get(0));
        result.put("selected", selectedActivity);
        result.put("activities", activeActivities);
        result.put("taskBoard", taskBoard);
        result.put("activityProgress", activityProgress);
        return result;
    }

    private ActivityTopic resolveSelectedActivity(List<ActivityTopic> activeActivities, String activityCode) {
        if (activeActivities == null || activeActivities.isEmpty()) {
            return null;
        }
        if (activityCode == null || activityCode.trim().isEmpty()) {
            return activeActivities.get(0);
        }
        String normalized = activityCode.trim().toUpperCase();
        for (ActivityTopic activity : activeActivities) {
            if (activity.getActivityCode() != null && normalized.equals(activity.getActivityCode().trim().toUpperCase())) {
                return activity;
            }
        }
        return activeActivities.get(0);
    }

    private Map<String, Object> buildActivityProgress(ActivityTopic activity, Map<String, Object> taskBoard) {
        Map<String, Object> progress = new HashMap<>();
        progress.put("activityCode", activity != null ? activity.getActivityCode() : "");
        progress.put("taskCount", 0);
        progress.put("completedCount", 0);
        progress.put("completionRate", 0);
        progress.put("progressText", "\u6682\u65e0\u4e3b\u7ebf\u4efb\u52a1");
        progress.put("totalRewardPoints", 0);
        progress.put("completedRewardPoints", 0);
        progress.put("nextTaskTitle", "");
        progress.put("nextActionText", "");
        progress.put("nextActionPath", "");
        progress.put("nextActionType", "");

        if (activity == null || taskBoard == null) {
            return progress;
        }

        List<TaskBoardItemVO> allTasks = safeTaskList(taskBoard.get("allTasks"));
        int taskCount = allTasks.size();
        int completedCount = 0;
        int totalRewardPoints = 0;
        int completedRewardPoints = 0;
        TaskBoardItemVO nextTask = null;

        for (TaskBoardItemVO task : allTasks) {
            totalRewardPoints += task.getRewardPoints() == null ? 0 : task.getRewardPoints();
            if (Boolean.TRUE.equals(task.getCompleted())) {
                completedCount++;
                completedRewardPoints += task.getRewardPoints() == null ? 0 : task.getRewardPoints();
            } else if (nextTask == null) {
                nextTask = task;
            }
        }

        int completionRate = taskCount <= 0 ? 0 : (int) Math.round((completedCount * 100.0) / taskCount);
        progress.put("taskCount", taskCount);
        progress.put("completedCount", completedCount);
        progress.put("completionRate", Math.min(completionRate, 100));
        progress.put("progressText", completedCount + " / " + taskCount + "\u4efb\u52a1\u5df2\u5b8c\u6210");
        progress.put("totalRewardPoints", totalRewardPoints);
        progress.put("completedRewardPoints", completedRewardPoints);

        if (nextTask != null) {
            progress.put("nextTaskTitle", nextTask.getTitle());
            progress.put("nextActionText", nextTask.getActionText());
            progress.put("nextActionPath", nextTask.getActionPath());
            progress.put("nextActionType", nextTask.getActionType());
        } else {
            progress.put("nextTaskTitle", "\u4e3b\u7ebf\u5df2\u5168\u90e8\u5b8c\u6210");
            progress.put("nextActionText", "\u53bb\u67e5\u770b\u6210\u5c31");
            progress.put("nextActionPath", "/pages/profile/profile");
            progress.put("nextActionType", "tab");
        }
        return progress;
    }

    @SuppressWarnings("unchecked")
    private List<TaskBoardItemVO> safeTaskList(Object value) {
        if (value instanceof List) {
            return (List<TaskBoardItemVO>) value;
        }
        return Collections.emptyList();
    }

    private Map<String, Object> emptyTaskBoard() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("dailyCompleted", 0);
        summary.put("dailyTotal", 0);
        summary.put("weeklyCompleted", 0);
        summary.put("weeklyTotal", 0);
        summary.put("dailyPeriodLabel", "");
        summary.put("weeklyPeriodLabel", "");
        summary.put("totalTaskCount", 0);
        summary.put("completedTaskCount", 0);
        summary.put("totalRewardPoints", 0);
        summary.put("completedRewardPoints", 0);

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("dailyTasks", Collections.emptyList());
        result.put("weeklyTasks", Collections.emptyList());
        result.put("allTasks", Collections.emptyList());
        return result;
    }

    private List<String> parseTaskCodes(ActivityTopic activity) {
        Set<String> codes = new LinkedHashSet<>();
        if (activity != null && activity.getTaskCodes() != null && !activity.getTaskCodes().trim().isEmpty()) {
            String[] parts = activity.getTaskCodes().split("[,;\\n\\r]+");
            for (String part : parts) {
                if (part != null && !part.trim().isEmpty()) {
                    codes.add(part.trim().toUpperCase());
                }
            }
        }
        if (!codes.isEmpty()) {
            return new ArrayList<>(codes);
        }
        return defaultTaskCodes(activity != null ? activity.getCoverEmoji() : null);
    }

    private List<String> defaultTaskCodes(String coverEmoji) {
        String coverKey = normalizeCoverKey(coverEmoji);
        List<String> codes = new ArrayList<>();
        if ("EARTH".equals(coverKey)) {
            codes.add("DAILY_STEP_6000");
            codes.add("DAILY_SPORT_2KM");
            codes.add("WEEKLY_REDEEM_1");
            return codes;
        }
        if ("RUN".equals(coverKey)) {
            codes.add("DAILY_SPORT_2KM");
            codes.add("WEEKLY_SPORT_3_TIMES");
            codes.add("DAILY_CHECKIN_1");
            return codes;
        }
        if ("BIKE".equals(coverKey)) {
            codes.add("DAILY_STEP_6000");
            codes.add("WEEKLY_STEP_50000");
            codes.add("DAILY_CHECKIN_1");
            return codes;
        }
        if ("TREE".equals(coverKey)) {
            codes.add("DAILY_CHECKIN_1");
            codes.add("DAILY_STEP_6000");
            codes.add("WEEKLY_REDEEM_1");
            return codes;
        }
        if ("CLEAN".equals(coverKey) || "RECYCLE".equals(coverKey)) {
            codes.add("DAILY_CHECKIN_1");
            codes.add("WEEKLY_REDEEM_1");
            codes.add("DAILY_STEP_6000");
            return codes;
        }
        if ("STAR".equals(coverKey)) {
            codes.add("DAILY_CHECKIN_1");
            codes.add("WEEKLY_REDEEM_1");
            codes.add("WEEKLY_STEP_50000");
            return codes;
        }
        codes.add("DAILY_STEP_6000");
        codes.add("DAILY_CHECKIN_1");
        codes.add("WEEKLY_STEP_50000");
        return codes;
    }

    private void normalizeActivityVisuals(ActivityTopic activity) {
        if (activity == null) {
            return;
        }
        activity.setCoverEmoji(resolveCoverEmoji(activity.getCoverEmoji()));
    }

    private String normalizeCoverKey(String coverKey) {
        if (coverKey == null || coverKey.trim().isEmpty()) {
            return "LEAF";
        }
        String normalized = coverKey.trim().toUpperCase();
        if ("\uD83C\uDF0D".equals(coverKey)) {
            return "EARTH";
        }
        if ("\uD83D\uDEB2".equals(coverKey)) {
            return "BIKE";
        }
        if ("\uD83C\uDF33".equals(coverKey)) {
            return "TREE";
        }
        if ("\uD83E\uDDF9".equals(coverKey)) {
            return "CLEAN";
        }
        if ("\u267B\uFE0F".equals(coverKey)) {
            return "RECYCLE";
        }
        if ("\uD83C\uDFC3".equals(coverKey)) {
            return "RUN";
        }
        if ("\u2B50".equals(coverKey)) {
            return "STAR";
        }
        if ("\uD83C\uDF3F".equals(coverKey)) {
            return "LEAF";
        }
        return normalized;
    }

    private String resolveCoverEmoji(String coverKey) {
        String normalized = normalizeCoverKey(coverKey);
        if ("EARTH".equals(normalized)) {
            return "\uD83C\uDF0D";
        }
        if ("BIKE".equals(normalized)) {
            return "\uD83D\uDEB2";
        }
        if ("TREE".equals(normalized)) {
            return "\uD83C\uDF33";
        }
        if ("CLEAN".equals(normalized)) {
            return "\uD83E\uDDF9";
        }
        if ("RECYCLE".equals(normalized)) {
            return "\u267B\uFE0F";
        }
        if ("RUN".equals(normalized)) {
            return "\uD83C\uDFC3";
        }
        if ("STAR".equals(normalized)) {
            return "\u2B50";
        }
        return "\uD83C\uDF3F";
    }
}
