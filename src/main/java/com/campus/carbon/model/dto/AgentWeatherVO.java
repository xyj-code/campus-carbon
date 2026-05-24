package com.campus.carbon.model.dto;

public class AgentWeatherVO {
    private String status;
    private String city;
    private String weather;
    private String temperature;
    private String windDirection;
    private String windPower;
    private String reportTime;
    private String suitability;
    private String recommendation;
    private Boolean outdoorFriendly;
    private Boolean indoorPreferred;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getSuitability() {
        return suitability;
    }

    public void setSuitability(String suitability) {
        this.suitability = suitability;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Boolean getOutdoorFriendly() {
        return outdoorFriendly;
    }

    public void setOutdoorFriendly(Boolean outdoorFriendly) {
        this.outdoorFriendly = outdoorFriendly;
    }

    public Boolean getIndoorPreferred() {
        return indoorPreferred;
    }

    public void setIndoorPreferred(Boolean indoorPreferred) {
        this.indoorPreferred = indoorPreferred;
    }
}
