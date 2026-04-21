package com.campus.carbon.model.dto;

public class AgentActionVO {
    private String id;
    private String title;
    private String reason;
    private String taskCode;
    private String priorityTag;
    private Integer durationMinutes;
    private Integer estimatedCarbonSaving;
    private Integer estimatedPoints;
    private String actionText;
    private String actionPath;
    private String actionType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getPriorityTag() {
        return priorityTag;
    }

    public void setPriorityTag(String priorityTag) {
        this.priorityTag = priorityTag;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getEstimatedCarbonSaving() {
        return estimatedCarbonSaving;
    }

    public void setEstimatedCarbonSaving(Integer estimatedCarbonSaving) {
        this.estimatedCarbonSaving = estimatedCarbonSaving;
    }

    public Integer getEstimatedPoints() {
        return estimatedPoints;
    }

    public void setEstimatedPoints(Integer estimatedPoints) {
        this.estimatedPoints = estimatedPoints;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
