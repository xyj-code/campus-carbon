package com.campus.carbon.model;

import java.util.Date;

public class Project {
    private Integer id;
    private String name;
    private String type;
    private String location;
    private String description;
    private Integer progress;
    private String images;
    private Double requiredCarbon;
    private Integer status;
    private Date createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public Double getRequiredCarbon() { return requiredCarbon; }
    public void setRequiredCarbon(Double requiredCarbon) { this.requiredCarbon = requiredCarbon; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}