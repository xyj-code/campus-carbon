package com.campus.carbon.model.dto;

public class AgentSummaryVO {
    private String title;
    private String status;
    private String reason;
    private String focusLabel;
    private String completionLabel;
    private Integer estimatedCarbonSaving;
    private Integer estimatedPoints;
    private String updatedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFocusLabel() {
        return focusLabel;
    }

    public void setFocusLabel(String focusLabel) {
        this.focusLabel = focusLabel;
    }

    public String getCompletionLabel() {
        return completionLabel;
    }

    public void setCompletionLabel(String completionLabel) {
        this.completionLabel = completionLabel;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
