package com.campus.carbon.model.dto;

import java.util.ArrayList;
import java.util.List;

public class AgentBriefVO {
    private String sessionId;
    private String sessionStatus;
    private String currentActionId;
    private AgentSummaryVO summary;
    private AgentSportPlanVO sportPlan;
    private AgentWeatherVO weather;
    private AgentScheduleVO schedule;
    private List<AgentActionVO> actions = new ArrayList<>();
    private List<String> evidence = new ArrayList<>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public String getCurrentActionId() {
        return currentActionId;
    }

    public void setCurrentActionId(String currentActionId) {
        this.currentActionId = currentActionId;
    }

    public AgentSummaryVO getSummary() {
        return summary;
    }

    public void setSummary(AgentSummaryVO summary) {
        this.summary = summary;
    }

    public AgentSportPlanVO getSportPlan() {
        return sportPlan;
    }

    public void setSportPlan(AgentSportPlanVO sportPlan) {
        this.sportPlan = sportPlan;
    }

    public AgentWeatherVO getWeather() {
        return weather;
    }

    public void setWeather(AgentWeatherVO weather) {
        this.weather = weather;
    }

    public AgentScheduleVO getSchedule() {
        return schedule;
    }

    public void setSchedule(AgentScheduleVO schedule) {
        this.schedule = schedule;
    }

    public List<AgentActionVO> getActions() {
        return actions;
    }

    public void setActions(List<AgentActionVO> actions) {
        this.actions = actions;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }
}
