package com.manager.nacelle_rent.entity;

import java.sql.Timestamp;
import java.util.ArrayList;

public class ElectricRes {
    private String deviceId;
    private String projectId;
    private String projectName;
    private String projectBuilders;
    private Timestamp timeStart;
    private ArrayList<SimpleUser> projectBuildersDetail;

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

    public String getProjectBuilders() {
        return projectBuilders;
    }

    public void setProjectBuilders(String projectBuilders) {
        this.projectBuilders = projectBuilders;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public ArrayList<SimpleUser> getProjectBuildersDetail() {
        return projectBuildersDetail;
    }

    public void setProjectBuildersDetail(ArrayList<SimpleUser> projectBuildersDetail) {
        this.projectBuildersDetail = projectBuildersDetail;
    }
}
