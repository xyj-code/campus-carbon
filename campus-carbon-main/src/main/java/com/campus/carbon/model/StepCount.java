package com.campus.carbon.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class StepCount {
    private Long id;
    private String studentId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    private Integer steps;
    private Double calories;
    private Integer duration;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Integer getSteps() { return steps; }
    public void setSteps(Integer steps) { this.steps = steps; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
}
