package com.manager.nacelle_rent.entity;

public class Project {
    private String projectId;
    private String projectName;
    private String projectState;
    private String projectStart;
    private String projectEnd;
    private String projectContractUrl; //项目合同Url
    private String projectCertUrl;    //项目证书Url
    private String adminAreaId;
    private String adminRentId;
    private String adminProjectId;
    private String boxList;
    private String worker;
    private String projectBuilders;
    private String storeOut;
    private String projectEndUrl;
    private String companyName;
    private String owner;
    private String region;
    private String coordinate;
    private String servicePeriod;
    private int deviceNum;
    private int workerNum;
    private User adminAreaUser;
    volatile private User adminRentUser;

    public String getAdminAreaId() {
        return adminAreaId;
    }

    public void setAdminAreaId(String adminAreaId) {
        this.adminAreaId = adminAreaId;
    }

    public String getAdminRentId() {
        return adminRentId;
    }

    public void setAdminRentId(String adminRentId) {
        this.adminRentId = adminRentId;
    }

    public String getAdminProjectId() {
        return adminProjectId;
    }

    public void setAdminProjectId(String adminProjectId) {
        this.adminProjectId = adminProjectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectState() {
        return projectState;
    }

    public void setProjectState(String projectState) {
        this.projectState = projectState;
    }

    public String getProjectStart() {
        return projectStart;
    }

    public void setProjectStart(String projectStart) {
        this.projectStart = projectStart;
    }

    public String getProjectEnd() {
        return projectEnd;
    }

    public void setProjectEnd(String projectEnd) {
        this.projectEnd = projectEnd;
    }

    public String getProjectCertUrl() {
        return projectCertUrl;
    }

    public void setProjectCertUrl(String projectCertUrl) {
        this.projectCertUrl = projectCertUrl;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBoxList() {
        return boxList;
    }

    public void setBoxList(String boxList) {
        this.boxList = boxList;
    }

    public String getProjectBuilders() {
        return projectBuilders;
    }

    public void setProjectBuilders(String projectBuilders) {
        this.projectBuilders = projectBuilders;
    }

    public String getProjectContractUrl() {
        return projectContractUrl;
    }

    public void setProjectContractUrl(String projectContractUrl) {
        this.projectContractUrl = projectContractUrl;
    }

    public User getAdminAreaUser() {
        return adminAreaUser;
    }

    public void setAdminAreaUser(User adminAreaUser) {
        this.adminAreaUser = adminAreaUser;
    }

    public User getAdminRentUser() {
        return adminRentUser;
    }

    public void setAdminRentUser(User adminRentUser) {
        this.adminRentUser = adminRentUser;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getStoreOut() { return storeOut; }

    public void setStoreOut(String storeOut) { this.storeOut = storeOut; }

    public String getProjectEndUrl() {
        return projectEndUrl;
    }

    public void setProjectEndUrl(String projectEndUrl) {
        this.projectEndUrl = projectEndUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public int getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(int deviceNum) {
        this.deviceNum = deviceNum;
    }

    public int getWorkerNum() {
        return workerNum;
    }

    public void setWorkerNum(int workerNum) {
        this.workerNum = workerNum;
    }

    public String getServicePeriod() {
        return servicePeriod;
    }

    public void setServicePeriod(String servicePeriod) {
        this.servicePeriod = servicePeriod;
    }
}
