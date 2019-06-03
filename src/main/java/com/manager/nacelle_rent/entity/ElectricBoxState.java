package com.manager.nacelle_rent.entity;

import java.sql.Timestamp;

public class ElectricBoxState {
    private int workingState;
    private int storageState;
    private int alarm;
    private Timestamp date;
    private String deviceId;
    private String projectId;
    private String storeIn;

    public int getWorkingState() { return workingState; }

    public void setWorkingState(int workingState) { this.workingState = workingState; }

    public int getStorageState() { return storageState; }

    public void setStorageState(int storageState) {
        this.storageState = storageState;
    }

    public int getAlarm() { return alarm; }

    public void setAlarm(int alarm) { this.alarm = alarm; }

    public Timestamp getDate() { return date; }

    public void setDate(Timestamp date) { this.date = date; }

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

    public String getStoreIn() {
        return storeIn;
    }

    public void setStoreIn(String storeIn) {
        this.storeIn = storeIn;
    }
}
