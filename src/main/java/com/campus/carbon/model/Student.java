package com.campus.carbon.model;

import java.util.Date;

public class Student {
    private Long id;
    private String stuNo;
    private String name;
    private String openid;
    private String password;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStuNo() { return stuNo; }
    public void setStuNo(String stuNo) { this.stuNo = stuNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
