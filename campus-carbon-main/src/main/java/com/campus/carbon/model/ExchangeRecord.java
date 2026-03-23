package com.campus.carbon.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class ExchangeRecord {
    private Long id;
    private String username;
    private Long productId;
    private String productName;
    private Integer point;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getPoint() { return point; }
    public void setPoint(Integer point) { this.point = point; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
