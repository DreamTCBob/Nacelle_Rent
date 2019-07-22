package com.manager.nacelle_rent.entity;

public class WorkTimeLog {
    private int id;
    private String deviceId;
    private String projectId;
    private String userId;
    private long timeWork;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeWork() {
        return timeWork;
    }

    public void setTimeWork(long timeWork) {
        this.timeWork = timeWork;
    }

}
