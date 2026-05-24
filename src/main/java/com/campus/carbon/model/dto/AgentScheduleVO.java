package com.campus.carbon.model.dto;

public class AgentScheduleVO {
    private String status;
    private String state;
    private String stateLabel;
    private String recommendation;
    private Integer weekday;
    private Integer todayCourseCount;
    private Integer freeMinutes;
    private String currentCourseName;
    private String currentCourseTime;
    private String nextCourseName;
    private String nextCourseTime;
    private Integer minutesToNextCourse;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateLabel() {
        return stateLabel;
    }

    public void setStateLabel(String stateLabel) {
        this.stateLabel = stateLabel;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public Integer getTodayCourseCount() {
        return todayCourseCount;
    }

    public void setTodayCourseCount(Integer todayCourseCount) {
        this.todayCourseCount = todayCourseCount;
    }

    public Integer getFreeMinutes() {
        return freeMinutes;
    }

    public void setFreeMinutes(Integer freeMinutes) {
        this.freeMinutes = freeMinutes;
    }

    public String getCurrentCourseName() {
        return currentCourseName;
    }

    public void setCurrentCourseName(String currentCourseName) {
        this.currentCourseName = currentCourseName;
    }

    public String getCurrentCourseTime() {
        return currentCourseTime;
    }

    public void setCurrentCourseTime(String currentCourseTime) {
        this.currentCourseTime = currentCourseTime;
    }

    public String getNextCourseName() {
        return nextCourseName;
    }

    public void setNextCourseName(String nextCourseName) {
        this.nextCourseName = nextCourseName;
    }

    public String getNextCourseTime() {
        return nextCourseTime;
    }

    public void setNextCourseTime(String nextCourseTime) {
        this.nextCourseTime = nextCourseTime;
    }

    public Integer getMinutesToNextCourse() {
        return minutesToNextCourse;
    }

    public void setMinutesToNextCourse(Integer minutesToNextCourse) {
        this.minutesToNextCourse = minutesToNextCourse;
    }
}
