package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.TaskTemplateMapper;
import com.campus.carbon.mapper.UserTaskMapper;
import com.campus.carbon.model.TaskTemplate;
import com.campus.carbon.service.AdminTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class AdminTaskServiceImpl implements AdminTaskService {

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    @Autowired
    private TaskTemplateMapper taskTemplateMapper;

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Override
    public Map<String, Object> getDashboard() {
      List<TaskTemplate> templates = taskTemplateMapper.selectAll();
      List<Map<String, Object>> cards = new ArrayList<>();

      for (TaskTemplate template : templates) {
          String periodKey = resolveCurrentPeriodKey(template.getPeriodType());
          int participants = userTaskMapper.countByTaskAndPeriod(template.getTaskCode(), periodKey);
          int completed = userTaskMapper.countCompletedByTaskAndPeriod(template.getTaskCode(), periodKey);
          int completionRate = participants <= 0 ? 0 : (int) Math.round((completed * 100.0) / participants);

          Map<String, Object> row = new HashMap<>();
          row.put("id", template.getId());
          row.put("taskCode", template.getTaskCode());
          row.put("taskName", resolveTaskName(template.getTaskCode()));
          row.put("taskDesc", resolveTaskDesc(template.getTaskCode()));
          row.put("periodType", template.getPeriodType());
          row.put("periodLabel", "WEEKLY".equals(template.getPeriodType()) ? "每周挑战" : "每日任务");
          row.put("metricType", template.getMetricType());
          row.put("metricLabel", resolveMetricLabel(template.getMetricType()));
          row.put("targetValue", template.getTargetValue());
          row.put("rewardPoints", template.getRewardPoints());
          row.put("status", template.getStatus());
          row.put("sortOrder", template.getSortOrder());
          row.put("participants", participants);
          row.put("completedUsers", completed);
          row.put("completionRate", completionRate);
          row.put("currentPeriodKey", periodKey);
          cards.add(row);
      }

      Map<String, Object> summary = new HashMap<>();
      summary.put("totalTasks", cards.size());
      summary.put("activeTasks", cards.stream().filter(item -> ((Integer) item.get("status")) == 1).count());
      summary.put("dailyTasks", cards.stream().filter(item -> "DAILY".equals(item.get("periodType"))).count());
      summary.put("weeklyTasks", cards.stream().filter(item -> "WEEKLY".equals(item.get("periodType"))).count());

      Map<String, Object> result = new HashMap<>();
      result.put("summary", summary);
      result.put("templates", cards);
      return result;
    }

    @Override
    @Transactional
    public Map<String, Object> saveTemplate(Map<String, Object> payload) {
        TaskTemplate template;
        Object idValue = payload.get("id");
        if (idValue != null && String.valueOf(idValue).trim().length() > 0) {
            template = taskTemplateMapper.selectById(Long.valueOf(String.valueOf(idValue)));
            if (template == null) {
                return error(404, "TASK_NOT_FOUND");
            }
        } else {
            template = new TaskTemplate();
            template.setStatus(1);
        }

        template.setTaskCode(stringValue(payload.get("taskCode")));
        template.setPeriodType(stringValue(payload.get("periodType")));
        template.setMetricType(stringValue(payload.get("metricType")));
        template.setTargetValue(doubleValue(payload.get("targetValue")));
        template.setRewardPoints(intValue(payload.get("rewardPoints")));
        template.setSortOrder(intValue(payload.get("sortOrder")));
        template.setStatus(intValue(payload.get("status")) == 0 ? 0 : 1);

        if (template.getId() == null) {
            taskTemplateMapper.insert(template);
        } else {
            taskTemplateMapper.update(template);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "OK");
        result.put("id", template.getId());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> toggleTemplate(Long id) {
        TaskTemplate template = taskTemplateMapper.selectById(id);
        if (template == null) {
            return error(404, "TASK_NOT_FOUND");
        }
        template.setStatus(template.getStatus() != null && template.getStatus() == 1 ? 0 : 1);
        taskTemplateMapper.update(template);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "OK");
        result.put("status", template.getStatus());
        return result;
    }

    private String resolveCurrentPeriodKey(String periodType) {
        LocalDate today = LocalDate.now(CHINA_ZONE);
        if ("WEEKLY".equals(periodType)) {
            int week = today.get(WeekFields.of(Locale.CHINA).weekOfWeekBasedYear());
            return today.getYear() + "-W" + String.format("%02d", week);
        }
        return today.toString();
    }

    private String resolveTaskName(String taskCode) {
        if ("DAILY_STEP_6000".equals(taskCode)) return "今日步行 6000 步";
        if ("DAILY_SPORT_2KM".equals(taskCode)) return "今日运动 2km";
        if ("DAILY_CHECKIN_1".equals(taskCode)) return "完成一次低碳打卡";
        if ("WEEKLY_STEP_50000".equals(taskCode)) return "本周累计 50000 步";
        if ("WEEKLY_SPORT_3_TIMES".equals(taskCode)) return "本周运动 3 次";
        if ("WEEKLY_REDEEM_1".equals(taskCode)) return "本周兑换 1 次绿色权益";
        return taskCode;
    }

    private String resolveTaskDesc(String taskCode) {
        if ("DAILY_STEP_6000".equals(taskCode)) return "用于引导学生完成日常绿色出行目标";
        if ("DAILY_SPORT_2KM".equals(taskCode)) return "把低碳生活和日常运动行为绑定";
        if ("DAILY_CHECKIN_1".equals(taskCode)) return "鼓励形成每天至少一次低碳记录";
        if ("WEEKLY_STEP_50000".equals(taskCode)) return "用于强化一周内的稳定步行习惯";
        if ("WEEKLY_SPORT_3_TIMES".equals(taskCode)) return "鼓励形成一周多次运动的节奏";
        if ("WEEKLY_REDEEM_1".equals(taskCode)) return "把积分体系和校园权益闭环打通";
        return "主题活动任务";
    }

    private String resolveMetricLabel(String metricType) {
        if ("STEP_TOTAL".equals(metricType)) return "累计步数";
        if ("SPORT_DISTANCE_TOTAL".equals(metricType)) return "累计运动距离";
        if ("ACTIVE_DAY_COUNT".equals(metricType)) return "低碳打卡次数";
        if ("SPORT_RECORD_COUNT".equals(metricType)) return "运动记录次数";
        if ("BENEFIT_REDEEM_COUNT".equals(metricType)) return "权益兑换次数";
        return metricType;
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int intValue(Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private double doubleValue(Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            return 0D;
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private Map<String, Object> error(int code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        return result;
    }
}
