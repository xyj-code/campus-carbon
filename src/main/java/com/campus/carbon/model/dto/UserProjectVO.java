package com.campus.carbon.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class UserProjectVO {
    private Integer id;
    private Integer projectId;
    private String projectName;
    private String projectType;
    private String uniqueCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date unlockTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }

    public String getUniqueCode() { return uniqueCode; }
    public void setUniqueCode(String uniqueCode) { this.uniqueCode = uniqueCode; }

    public Date getUnlockTime() { return unlockTime; }
    public void setUnlockTime(Date unlockTime) { this.unlockTime = unlockTime; }
}