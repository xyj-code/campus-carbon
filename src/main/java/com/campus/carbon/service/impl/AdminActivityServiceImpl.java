package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.ActivityTopicMapper;
import com.campus.carbon.model.ActivityTopic;
import com.campus.carbon.service.AdminActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdminActivityServiceImpl implements AdminActivityService {

    private static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ActivityTopicMapper activityTopicMapper;

    @Override
    public Map<String, Object> getDashboard() {
        List<ActivityTopic> activities = activityTopicMapper.selectAll();
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalActivities", activities.size());
        summary.put("activeActivities", activities.stream().filter(item -> item.getStatus() != null && item.getStatus() == 1).count());
        summary.put("featuredActivities", activities.stream().filter(item -> item.getFeatured() != null && item.getFeatured() == 1).count());

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("activities", activities);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> saveActivity(Map<String, Object> payload) {
        ActivityTopic activity;
        Object idValue = payload.get("id");
        if (idValue != null && String.valueOf(idValue).trim().length() > 0) {
            activity = activityTopicMapper.selectById(Long.valueOf(String.valueOf(idValue)));
            if (activity == null) {
                return error(404, "ACTIVITY_NOT_FOUND");
            }
        } else {
            activity = new ActivityTopic();
            activity.setStatus(1);
            activity.setFeatured(0);
        }

        String title = stringValue(payload.get("title"));
        String subtitle = stringValue(payload.get("subtitle"));
        String description = stringValue(payload.get("description"));
        String bannerTitle = stringValue(payload.get("bannerTitle"));
        String bannerSubtitle = stringValue(payload.get("bannerSubtitle"));
        String rewardText = stringValue(payload.get("rewardText"));
        String targetText = stringValue(payload.get("targetText"));
        String themeColor = stringValue(payload.get("themeColor"));

        if (title.isEmpty()) {
            return error(400, "TITLE_REQUIRED");
        }

        activity.setTitle(title);
        activity.setSubtitle(subtitle);
        activity.setDescription(description);
        activity.setBannerTitle(bannerTitle.isEmpty() ? title : bannerTitle);
        activity.setBannerSubtitle(resolveBannerSubtitle(subtitle, description, bannerSubtitle));
        activity.setRewardText(rewardText.isEmpty() ? "\u53ef\u89e3\u9501\u79ef\u5206\u3001\u6743\u76ca\u4e0e\u6210\u5c31\u5956\u52b1" : rewardText);
        activity.setTargetText(targetText.isEmpty() ? "\u9762\u5411\u81ea\u5b9a\u4e49\u4f4e\u78b3\u6d3b\u52a8\u573a\u666f" : targetText);
        activity.setThemeColor(themeColor.isEmpty() ? "#3f9a63" : themeColor);
        activity.setActivityCode(resolveActivityCode(payload.get("activityCode"), title, activity.getId()));
        String coverKey = resolveCoverKey(payload.get("coverEmoji"), title, subtitle, description);
        activity.setCoverEmoji(coverKey);
        activity.setTaskCodes(resolveTaskCodes(payload.get("taskCodes"), coverKey));
        activity.setStatus(intValue(payload.get("status")) == 0 ? 0 : 1);
        activity.setFeatured(intValue(payload.get("featured")) == 1 ? 1 : 0);
        activity.setSortOrder(intValue(payload.get("sortOrder")));
        activity.setStartTime(dateValue(payload.get("startTime")));
        activity.setEndTime(dateValue(payload.get("endTime")));

        int affectedRows;
        if (activity.getId() == null) {
            affectedRows = activityTopicMapper.insert(activity);
        } else {
            affectedRows = activityTopicMapper.update(activity);
        }

        if (affectedRows <= 0) {
            return error(500, "ACTIVITY_SAVE_FAILED");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "OK");
        result.put("id", activity.getId());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteActivity(Long id) {
        activityTopicMapper.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "OK");
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> toggleActivity(Long id) {
        ActivityTopic activity = activityTopicMapper.selectById(id);
        if (activity == null) {
            return error(404, "ACTIVITY_NOT_FOUND");
        }
        activity.setStatus(activity.getStatus() != null && activity.getStatus() == 1 ? 0 : 1);
        activityTopicMapper.update(activity);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "OK");
        result.put("status", activity.getStatus());
        return result;
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String resolveActivityCode(Object rawCode, String title, Long id) {
        String code = stringValue(rawCode);
        if (!code.isEmpty()) {
            return code.toUpperCase();
        }
        String normalizedTitle = title == null ? "" : title.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_+|_+$", "");
        String suffix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (!normalizedTitle.isEmpty()) {
            return (normalizedTitle + "_" + suffix).toUpperCase();
        }
        if (id != null) {
            return "ACTIVITY_" + id;
        }
        return "ACTIVITY_" + suffix;
    }

    private String resolveBannerSubtitle(String subtitle, String description, String bannerSubtitle) {
        if (!bannerSubtitle.isEmpty()) {
            return bannerSubtitle;
        }
        if (!subtitle.isEmpty()) {
            return subtitle;
        }
        if (!description.isEmpty()) {
            return description.length() > 36 ? description.substring(0, 36) + "..." : description;
        }
        return "\u53ef\u4ee5\u7528\u4e8e\u690d\u6811\u3001\u6361\u5783\u573e\u3001\u7eff\u8272\u51fa\u884c\u7b49\u81ea\u5b9a\u4e49\u4f4e\u78b3\u4e13\u9898";
    }

    private String resolveCoverKey(Object rawCoverValue, String title, String subtitle, String description) {
        String coverValue = stringValue(rawCoverValue);
        String normalized = normalizeCoverKey(coverValue);
        if (!normalized.isEmpty()) {
            return normalized;
        }
        String text = (title + " " + subtitle + " " + description).toLowerCase();
        if (containsAny(text, "\u690d\u6811", "\u6811", "tree", "plant")) {
            return "TREE";
        }
        if (containsAny(text, "\u5783\u573e", "\u6e05\u6d01", "\u51c0\u6ee9", "\u6253\u626b", "clean", "trash")) {
            return "CLEAN";
        }
        if (containsAny(text, "\u56de\u6536", "\u5faa\u73af", "recycle")) {
            return "RECYCLE";
        }
        if (containsAny(text, "\u8dd1\u6b65", "\u8fd0\u52a8", "\u5f92\u6b65", "run", "sport")) {
            return "RUN";
        }
        if (containsAny(text, "\u9a91\u884c", "\u51fa\u884c", "bike", "ride", "commute")) {
            return "BIKE";
        }
        if (containsAny(text, "\u5730\u7403", "\u73af\u4fdd", "earth")) {
            return "EARTH";
        }
        if (containsAny(text, "\u8363\u8a89", "\u8868\u5f70", "star", "award")) {
            return "STAR";
        }
        return "LEAF";
    }

    private String resolveTaskCodes(Object rawTaskCodes, String coverKey) {
        String taskCodes = stringValue(rawTaskCodes);
        Set<String> normalizedCodes = new LinkedHashSet<>();
        if (!taskCodes.isEmpty()) {
            String[] parts = taskCodes.split("[,;\\n\\r]+");
            for (String part : parts) {
                if (part != null && !part.trim().isEmpty()) {
                    normalizedCodes.add(part.trim().toUpperCase());
                }
            }
        }
        if (normalizedCodes.isEmpty()) {
            if ("EARTH".equals(coverKey)) {
                normalizedCodes.add("DAILY_STEP_6000");
                normalizedCodes.add("DAILY_SPORT_2KM");
                normalizedCodes.add("WEEKLY_REDEEM_1");
            } else if ("RUN".equals(coverKey)) {
                normalizedCodes.add("DAILY_SPORT_2KM");
                normalizedCodes.add("WEEKLY_SPORT_3_TIMES");
                normalizedCodes.add("DAILY_CHECKIN_1");
            } else if ("BIKE".equals(coverKey)) {
                normalizedCodes.add("DAILY_STEP_6000");
                normalizedCodes.add("WEEKLY_STEP_50000");
                normalizedCodes.add("DAILY_CHECKIN_1");
            } else if ("TREE".equals(coverKey)) {
                normalizedCodes.add("DAILY_CHECKIN_1");
                normalizedCodes.add("DAILY_STEP_6000");
                normalizedCodes.add("WEEKLY_REDEEM_1");
            } else if ("CLEAN".equals(coverKey) || "RECYCLE".equals(coverKey)) {
                normalizedCodes.add("DAILY_CHECKIN_1");
                normalizedCodes.add("WEEKLY_REDEEM_1");
                normalizedCodes.add("DAILY_STEP_6000");
            } else if ("STAR".equals(coverKey)) {
                normalizedCodes.add("DAILY_CHECKIN_1");
                normalizedCodes.add("WEEKLY_REDEEM_1");
                normalizedCodes.add("WEEKLY_STEP_50000");
            } else {
                normalizedCodes.add("DAILY_STEP_6000");
                normalizedCodes.add("DAILY_CHECKIN_1");
                normalizedCodes.add("WEEKLY_STEP_50000");
            }
        }
        return String.join(",", normalizedCodes);
    }

    private String normalizeCoverKey(String coverValue) {
        if (coverValue == null || coverValue.trim().isEmpty()) {
            return "";
        }
        String normalized = coverValue.trim().toUpperCase();
        if ("\uD83C\uDF0D".equals(coverValue) || "EARTH".equals(normalized)) {
            return "EARTH";
        }
        if ("\uD83D\uDEB2".equals(coverValue) || "BIKE".equals(normalized)) {
            return "BIKE";
        }
        if ("\uD83C\uDF3F".equals(coverValue) || "LEAF".equals(normalized)) {
            return "LEAF";
        }
        if ("\uD83C\uDF33".equals(coverValue) || "TREE".equals(normalized)) {
            return "TREE";
        }
        if ("\uD83E\uDDF9".equals(coverValue) || "CLEAN".equals(normalized)) {
            return "CLEAN";
        }
        if ("\u267B\uFE0F".equals(coverValue) || "RECYCLE".equals(normalized)) {
            return "RECYCLE";
        }
        if ("\uD83C\uDFC3".equals(coverValue) || "RUN".equals(normalized)) {
            return "RUN";
        }
        if ("\u2B50".equals(coverValue) || "STAR".equals(normalized)) {
            return "STAR";
        }
        return normalized;
    }

    private boolean containsAny(String source, String... words) {
        if (source == null || source.isEmpty()) {
            return false;
        }
        for (String word : words) {
            if (source.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private int intValue(Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private java.util.Date dateValue(Object value) {
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            return null;
        }
        try {
            return DATE_TIME.parse(String.valueOf(value).trim());
        } catch (ParseException e) {
            return null;
        }
    }

    private Map<String, Object> error(int code, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        return result;
    }
}
