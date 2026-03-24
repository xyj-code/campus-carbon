package com.campus.carbon.model.dto;

import java.util.List;

public class ProjectDetailVO {
    private Integer id;
    private String name;
    private String location;
    private String description;
    private Integer progress;
    private List<String> images;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}