package com.campus.carbon.model;

import java.util.Date;

public class UserProject {
    private Integer id;
    private Integer userId;
    private Integer projectId;
    private String uniqueCode;
    private Date unlockTime;
    private Date createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }

    public String getUniqueCode() { return uniqueCode; }
    public void setUniqueCode(String uniqueCode) { this.uniqueCode = uniqueCode; }

    public Date getUnlockTime() { return unlockTime; }
    public void setUnlockTime(Date unlockTime) { this.unlockTime = unlockTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}