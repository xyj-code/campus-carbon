package com.campus.carbon.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class AgentMemory {
    private Long id;
    private String username;
    private String lastSessionId;
    private String lastUserNote;
    private Integer verifiedCompletionCount;
    private Integer claimedCompletionCount;
    private Integer skippedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastActiveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLastSessionId() { return lastSessionId; }
    public void setLastSessionId(String lastSessionId) { this.lastSessionId = lastSessionId; }

    public String getLastUserNote() { return lastUserNote; }
    public void setLastUserNote(String lastUserNote) { this.lastUserNote = lastUserNote; }

    public Integer getVerifiedCompletionCount() { return verifiedCompletionCount; }
    public void setVerifiedCompletionCount(Integer verifiedCompletionCount) { this.verifiedCompletionCount = verifiedCompletionCount; }

    public Integer getClaimedCompletionCount() { return claimedCompletionCount; }
    public void setClaimedCompletionCount(Integer claimedCompletionCount) { this.claimedCompletionCount = claimedCompletionCount; }

    public Integer getSkippedCount() { return skippedCount; }
    public void setSkippedCount(Integer skippedCount) { this.skippedCount = skippedCount; }

    public Date getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(Date lastActiveTime) { this.lastActiveTime = lastActiveTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
