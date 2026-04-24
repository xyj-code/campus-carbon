package com.campus.carbon.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class AgentActionLog {
    private Long id;
    private String sessionId;
    private String actionId;
    private String taskCode;
    private Integer stepOrder;
    private String title;
    private String reason;
    private String priorityTag;
    private Integer durationMinutes;
    private Integer estimatedCarbonSaving;
    private Integer estimatedPoints;
    private String actionText;
    private String actionPath;
    private String actionType;
    private String status;
    private String resultNote;
    private String completionMode;
    private String verificationStatus;
    private String executionHint;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }

    public String getTaskCode() { return taskCode; }
    public void setTaskCode(String taskCode) { this.taskCode = taskCode; }

    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPriorityTag() { return priorityTag; }
    public void setPriorityTag(String priorityTag) { this.priorityTag = priorityTag; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getEstimatedCarbonSaving() { return estimatedCarbonSaving; }
    public void setEstimatedCarbonSaving(Integer estimatedCarbonSaving) { this.estimatedCarbonSaving = estimatedCarbonSaving; }

    public Integer getEstimatedPoints() { return estimatedPoints; }
    public void setEstimatedPoints(Integer estimatedPoints) { this.estimatedPoints = estimatedPoints; }

    public String getActionText() { return actionText; }
    public void setActionText(String actionText) { this.actionText = actionText; }

    public String getActionPath() { return actionPath; }
    public void setActionPath(String actionPath) { this.actionPath = actionPath; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResultNote() { return resultNote; }
    public void setResultNote(String resultNote) { this.resultNote = resultNote; }

    public String getCompletionMode() { return completionMode; }
    public void setCompletionMode(String completionMode) { this.completionMode = completionMode; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getExecutionHint() { return executionHint; }
    public void setExecutionHint(String executionHint) { this.executionHint = executionHint; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
