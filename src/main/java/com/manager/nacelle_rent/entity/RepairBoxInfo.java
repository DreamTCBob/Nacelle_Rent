package com.manager.nacelle_rent.entity;

import java.sql.Timestamp;

public class RepairBoxInfo {
    private int id;
    private Timestamp startTime;
    private Timestamp endTime;
    private String startTimeS;
    private String endTimeS;
    private String deviceId;
    private String projectId;
    private String managerId;
    private String dealerId;
    private String reason;
    private String description;
    private String imageStart;
    private String imageEnd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getImageStart() {
        return imageStart;
    }

    public void setImageStart(String imageStart) {
        this.imageStart = imageStart;
    }

    public String getImageEnd() {
        return imageEnd;
    }

    public void setImageEnd(String imageEnd) {
        this.imageEnd = imageEnd;
    }

    public String getStartTimeS() {
        return startTimeS;
    }

    public void setStartTimeS(String startTimeS) {
        this.startTimeS = startTimeS;
    }

    public String getEndTimeS() {
        return endTimeS;
    }

    public void setEndTimeS(String endTimeS) {
        this.endTimeS = endTimeS;
    }
}
