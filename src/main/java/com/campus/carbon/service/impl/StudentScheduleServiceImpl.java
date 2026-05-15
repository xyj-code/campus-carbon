package com.campus.carbon.service.impl;

import com.campus.carbon.mapper.StudentScheduleMapper;
import com.campus.carbon.model.StudentSchedule;
import com.campus.carbon.model.dto.AgentScheduleVO;
import com.campus.carbon.service.StudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class StudentScheduleServiceImpl implements StudentScheduleService {

    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");

    @Autowired
    private StudentScheduleMapper studentScheduleMapper;

    @Override
    public List<StudentSchedule> listByUsername(String username) {
        return studentScheduleMapper.selectByUsername(safeText(username));
    }

    @Override
    public List<StudentSchedule> listToday(String username) {
        return studentScheduleMapper.selectByUsernameAndWeekday(safeText(username), todayWeekday());
    }

    @Override
    public StudentSchedule save(StudentSchedule schedule) {
        validateSchedule(schedule);
        if (schedule.getId() == null) {
            studentScheduleMapper.insert(schedule);
            return studentScheduleMapper.selectByIdAndUsername(schedule.getId(), schedule.getUsername());
        }
        int affected = studentScheduleMapper.update(schedule);
        if (affected <= 0) {
            throw new IllegalArgumentException("schedule not found");
        }
        return studentScheduleMapper.selectByIdAndUsername(schedule.getId(), schedule.getUsername());
    }

    @Override
    public boolean delete(Long id, String username) {
        if (id == null || safeText(username).isEmpty()) {
            return false;
        }
        return studentScheduleMapper.deleteByIdAndUsername(id, safeText(username)) > 0;
    }

    @Override
    public AgentScheduleVO getTodayContext(String username) {
        AgentScheduleVO context = new AgentScheduleVO();
        String normalizedUsername = safeText(username);
        if (normalizedUsername.isEmpty()) {
            context.setStatus("login_required");
            context.setState("unknown");
            context.setStateLabel("\u672a\u767b\u5f55");
            context.setRecommendation("\u767b\u5f55\u540e Agent \u624d\u80fd\u8bfb\u53d6\u4eca\u65e5\u8bfe\u8868\u5e76\u8c03\u6574\u4efb\u52a1\u8282\u594f\u3002");
            return context;
        }

        int weekday = todayWeekday();
        List<StudentSchedule> todayCourses = studentScheduleMapper.selectByUsernameAndWeekday(normalizedUsername, weekday);
        context.setStatus("ready");
        context.setWeekday(weekday);
        context.setTodayCourseCount(todayCourses == null ? 0 : todayCourses.size());
        if (todayCourses == null || todayCourses.isEmpty()) {
            context.setState("no_course");
            context.setStateLabel("\u4eca\u65e5\u65e0\u8bfe");
            context.setFreeMinutes(180);
            context.setRecommendation("\u4eca\u65e5\u6682\u65e0\u8bfe\u8868\u538b\u529b\uff0cAgent \u53ef\u4ee5\u63a8\u8350\u8fd0\u52a8\u8def\u7ebf\u6216\u8f83\u5b8c\u6574\u7684\u4f4e\u78b3\u4efb\u52a1\u3002");
            return context;
        }

        LocalTime now = LocalTime.now(CHINA_ZONE);
        StudentSchedule current = null;
        StudentSchedule next = null;
        for (StudentSchedule course : todayCourses) {
            LocalTime start = parseTime(course.getStartTime());
            LocalTime end = parseTime(course.getEndTime());
            if (start == null || end == null) {
                continue;
            }
            if (!now.isBefore(start) && now.isBefore(end)) {
                current = course;
                break;
            }
            if (now.isBefore(start)) {
                next = course;
                break;
            }
        }

        if (current != null) {
            LocalTime end = parseTime(current.getEndTime());
            context.setState("in_class");
            context.setStateLabel("\u6b63\u5728\u4e0a\u8bfe");
            context.setCurrentCourseName(current.getCourseName());
            context.setCurrentCourseTime(formatCourseTime(current));
            context.setFreeMinutes(end == null ? 0 : Math.max(0, (int) ChronoUnit.MINUTES.between(now, end)));
            context.setRecommendation("\u5f53\u524d\u5904\u4e8e\u4e0a\u8bfe\u65f6\u6bb5\uff0cAgent \u4f1a\u4f18\u5148\u63a8\u8350\u8bfe\u540e\u53ef\u6267\u884c\u7684\u77ed\u4efb\u52a1\u6216\u5ba4\u5185\u8f7b\u52a8\u4f5c\u3002");
            return context;
        }

        if (next != null) {
            int minutes = Math.max(0, (int) ChronoUnit.MINUTES.between(now, parseTime(next.getStartTime())));
            context.setNextCourseName(next.getCourseName());
            context.setNextCourseTime(formatCourseTime(next));
            context.setMinutesToNextCourse(minutes);
            context.setFreeMinutes(minutes);
            if (minutes <= 30) {
                context.setState("before_class");
                context.setStateLabel("\u5373\u5c06\u4e0a\u8bfe");
                context.setRecommendation("\u8ddd\u79bb\u4e0b\u4e00\u8282\u8bfe\u8f83\u8fd1\uff0cAgent \u4f1a\u907f\u514d\u63a8\u8350\u8fdc\u8ddd\u79bb\u573a\u5730\uff0c\u4f18\u5148\u5b89\u6392\u7b7e\u5230\u3001\u5065\u5eb7\u8bb0\u5f55\u7b49\u77ed\u52a8\u4f5c\u3002");
            } else if (minutes <= 90) {
                context.setState("short_break");
                context.setStateLabel("\u8bfe\u95f4\u7a7a\u6863");
                context.setRecommendation("\u5f53\u524d\u662f\u8bfe\u95f4\u7a7a\u6863\uff0cAgent \u4f1a\u4f18\u5148\u63a8\u8350\u77ed\u8def\u7ebf\u6b65\u884c\u6216\u4f4e\u6469\u64e6\u4efb\u52a1\u3002");
            } else {
                context.setState("long_free");
                context.setStateLabel("\u5927\u5757\u7a7a\u95f2");
                context.setRecommendation("\u8ddd\u79bb\u4e0b\u8282\u8bfe\u8fd8\u6709\u8f83\u957f\u65f6\u95f4\uff0cAgent \u53ef\u4ee5\u5b89\u6392\u8dd1\u6b65\u3001\u7403\u7c7b\u573a\u5730\u6216\u5468\u4efb\u52a1\u3002");
            }
            return context;
        }

        context.setState("after_class");
        context.setStateLabel("\u4eca\u65e5\u8bfe\u7a0b\u5df2\u7ed3\u675f");
        context.setFreeMinutes(120);
        context.setRecommendation("\u4eca\u65e5\u8bfe\u7a0b\u5df2\u7ed3\u675f\uff0cAgent \u53ef\u4ee5\u63a8\u8350\u665a\u95f4\u6563\u6b65\u3001\u8fd0\u52a8\u8def\u7ebf\u6216\u8f83\u5b8c\u6574\u7684\u4f4e\u78b3\u4efb\u52a1\u3002");
        return context;
    }

    private void validateSchedule(StudentSchedule schedule) {
        if (schedule == null || safeText(schedule.getUsername()).isEmpty()) {
            throw new IllegalArgumentException("username required");
        }
        if (schedule.getWeekday() == null || schedule.getWeekday() < 1 || schedule.getWeekday() > 7) {
            throw new IllegalArgumentException("weekday must be 1-7");
        }
        if (safeText(schedule.getCourseName()).isEmpty()) {
            throw new IllegalArgumentException("courseName required");
        }
        if (!TIME_PATTERN.matcher(safeText(schedule.getStartTime())).matches()
                || !TIME_PATTERN.matcher(safeText(schedule.getEndTime())).matches()) {
            throw new IllegalArgumentException("time must be HH:mm");
        }
        LocalTime start = parseTime(schedule.getStartTime());
        LocalTime end = parseTime(schedule.getEndTime());
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("startTime must before endTime");
        }
        schedule.setUsername(safeText(schedule.getUsername()));
        schedule.setCourseName(safeText(schedule.getCourseName()));
        schedule.setLocation(safeText(schedule.getLocation()));
        schedule.setRemark(safeText(schedule.getRemark()));
    }

    private int todayWeekday() {
        DayOfWeek dayOfWeek = LocalDate.now(CHINA_ZONE).getDayOfWeek();
        return dayOfWeek.getValue();
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(safeText(value));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String formatCourseTime(StudentSchedule course) {
        return safeText(course.getStartTime()) + "-" + safeText(course.getEndTime());
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}
