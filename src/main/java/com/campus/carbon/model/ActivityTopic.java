package com.campus.carbon.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ActivityTopic {
    private Long id;
    private String activityCode;
    private String title;
    private String subtitle;
    private String description;
    private String bannerTitle;
    private String bannerSubtitle;
    private String coverEmoji;
    private String themeColor;
    private String rewardText;
    private String targetText;
    private String taskCodes;
    private Integer status;
    private Integer featured;
    private Integer sortOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActivityCode() { return activityCode; }
    public void setActivityCode(String activityCode) { this.activityCode = activityCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBannerTitle() { return bannerTitle; }
    public void setBannerTitle(String bannerTitle) { this.bannerTitle = bannerTitle; }

    public String getBannerSubtitle() { return bannerSubtitle; }
    public void setBannerSubtitle(String bannerSubtitle) { this.bannerSubtitle = bannerSubtitle; }

    public String getCoverEmoji() { return coverEmoji; }
    public void setCoverEmoji(String coverEmoji) { this.coverEmoji = coverEmoji; }

    public String getThemeColor() { return themeColor; }
    public void setThemeColor(String themeColor) { this.themeColor = themeColor; }

    public String getRewardText() { return rewardText; }
    public void setRewardText(String rewardText) { this.rewardText = rewardText; }

    public String getTargetText() { return targetText; }
    public void setTargetText(String targetText) { this.targetText = targetText; }

    public String getTaskCodes() { return taskCodes; }
    public void setTaskCodes(String taskCodes) { this.taskCodes = taskCodes; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getFeatured() { return featured; }
    public void setFeatured(Integer featured) { this.featured = featured; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
