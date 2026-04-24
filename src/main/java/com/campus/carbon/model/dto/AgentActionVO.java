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
    private Integer stepOrder;
    private String status;
    private String statusLabel;
    private String executionHint;
    private String resultNote;
    private String completionMode;
    private String verificationStatus;
    private Boolean canStart;
    private Boolean canFinish;
    private Boolean canSkip;

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

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getExecutionHint() {
        return executionHint;
    }

    public void setExecutionHint(String executionHint) {
        this.executionHint = executionHint;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public String getCompletionMode() {
        return completionMode;
    }

    public void setCompletionMode(String completionMode) {
        this.completionMode = completionMode;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Boolean getCanStart() {
        return canStart;
    }

    public void setCanStart(Boolean canStart) {
        this.canStart = canStart;
    }

    public Boolean getCanFinish() {
        return canFinish;
    }

    public void setCanFinish(Boolean canFinish) {
        this.canFinish = canFinish;
    }

    public Boolean getCanSkip() {
        return canSkip;
    }

    public void setCanSkip(Boolean canSkip) {
        this.canSkip = canSkip;
    }
}
