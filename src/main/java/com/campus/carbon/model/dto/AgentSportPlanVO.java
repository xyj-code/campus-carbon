package com.campus.carbon.model.dto;

import java.util.ArrayList;
import java.util.List;

public class AgentSportPlanVO {
    private String status;
    private String sportType;
    private String sportLabel;
    private String queryText;
    private String venueName;
    private String venueAddress;
    private String venueType;
    private Integer venueDistanceMeters;
    private Integer routeDistanceMeters;
    private Integer routeDurationMinutes;
    private String transportMode;
    private Double originLatitude;
    private Double originLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String recommendationReason;
    private List<String> routeSteps = new ArrayList<>();
    private List<AgentRoutePointVO> routePolyline = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getSportLabel() {
        return sportLabel;
    }

    public void setSportLabel(String sportLabel) {
        this.sportLabel = sportLabel;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public Integer getVenueDistanceMeters() {
        return venueDistanceMeters;
    }

    public void setVenueDistanceMeters(Integer venueDistanceMeters) {
        this.venueDistanceMeters = venueDistanceMeters;
    }

    public Integer getRouteDistanceMeters() {
        return routeDistanceMeters;
    }

    public void setRouteDistanceMeters(Integer routeDistanceMeters) {
        this.routeDistanceMeters = routeDistanceMeters;
    }

    public Integer getRouteDurationMinutes() {
        return routeDurationMinutes;
    }

    public void setRouteDurationMinutes(Integer routeDurationMinutes) {
        this.routeDurationMinutes = routeDurationMinutes;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public Double getOriginLatitude() {
        return originLatitude;
    }

    public void setOriginLatitude(Double originLatitude) {
        this.originLatitude = originLatitude;
    }

    public Double getOriginLongitude() {
        return originLongitude;
    }

    public void setOriginLongitude(Double originLongitude) {
        this.originLongitude = originLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public List<String> getRouteSteps() {
        return routeSteps;
    }

    public void setRouteSteps(List<String> routeSteps) {
        this.routeSteps = routeSteps;
    }

    public List<AgentRoutePointVO> getRoutePolyline() {
        return routePolyline;
    }

    public void setRoutePolyline(List<AgentRoutePointVO> routePolyline) {
        this.routePolyline = routePolyline;
    }
}
