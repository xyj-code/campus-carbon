package com.campus.carbon.model;

import java.util.Date;

public class UserTree {
    private Long id;
    private String username;
    private Integer treeLevel;
    private String treeStage;
    private String treeName;
    private Integer growthValue;
    private Integer waterValue;
    private Double lastTotalCarbon;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getTreeLevel() { return treeLevel; }
    public void setTreeLevel(Integer treeLevel) { this.treeLevel = treeLevel; }

    public String getTreeStage() { return treeStage; }
    public void setTreeStage(String treeStage) { this.treeStage = treeStage; }

    public String getTreeName() { return treeName; }
    public void setTreeName(String treeName) { this.treeName = treeName; }

    public Integer getGrowthValue() { return growthValue; }
    public void setGrowthValue(Integer growthValue) { this.growthValue = growthValue; }

    public Integer getWaterValue() { return waterValue; }
    public void setWaterValue(Integer waterValue) { this.waterValue = waterValue; }

    public Double getLastTotalCarbon() { return lastTotalCarbon; }
    public void setLastTotalCarbon(Double lastTotalCarbon) { this.lastTotalCarbon = lastTotalCarbon; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
