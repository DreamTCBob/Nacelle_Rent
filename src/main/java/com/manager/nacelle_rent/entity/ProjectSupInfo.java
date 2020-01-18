package com.manager.nacelle_rent.entity;

public class ProjectSupInfo {

    private String projectId;
    private String projectName;
    private String constructionUnit;
    private String supervisionUnit;
    private String nacelleInspectionUnit;
    private String curtainWallConsultantUnit;
    private float maxMeters;
    private int maxLayers;
    private float area;
    private int buildingNumbers;

    public float getMaxMeters() {
        return maxMeters;
    }

    public void setMaxMeters(float maxMeters) {
        this.maxMeters = maxMeters;
    }

    public int getMaxLayers() {
        return maxLayers;
    }

    public void setMaxLayers(int maxLayers) {
        this.maxLayers = maxLayers;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getBuildingNumbers() {
        return buildingNumbers;
    }

    public void setBuildingNumbers(int buildingNumbers) {
        this.buildingNumbers = buildingNumbers;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getConstructionUnit() {
        return constructionUnit;
    }

    public void setConstructionUnit(String constructionUnit) {
        this.constructionUnit = constructionUnit;
    }

    public String getSupervisionUnit() {
        return supervisionUnit;
    }

    public void setSupervisionUnit(String supervisionUnit) {
        this.supervisionUnit = supervisionUnit;
    }

    public String getNacelleInspectionUnit() {
        return nacelleInspectionUnit;
    }

    public void setNacelleInspectionUnit(String nacelleInspectionUnit) {
        this.nacelleInspectionUnit = nacelleInspectionUnit;
    }

    public String getCurtainWallConsultantUnit() {
        return curtainWallConsultantUnit;
    }

    public void setCurtainWallConsultantUnit(String curtainWallConsultantUnit) {
        this.curtainWallConsultantUnit = curtainWallConsultantUnit;
    }
}
