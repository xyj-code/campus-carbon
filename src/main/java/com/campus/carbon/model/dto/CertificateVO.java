package com.campus.carbon.model.dto;

public class CertificateVO {
    private String userName;
    private String projectName;
    private String uniqueCode;
    private String unlockTime;
    private String certificateUrl;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getUniqueCode() { return uniqueCode; }
    public void setUniqueCode(String uniqueCode) { this.uniqueCode = uniqueCode; }

    public String getUnlockTime() { return unlockTime; }
    public void setUnlockTime(String unlockTime) { this.unlockTime = unlockTime; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }
}