package com.manager.nacelle_rent.entity;

public class RealTimeData {
    private String device_id;
    private String bool_data_int32;
    private String current;
    private String degree;
    private double longitude;
    private double latitude;
    private double altitude;
    private String timestamp;
    private double weight;

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String deviceId) {
        this.device_id = deviceId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getBool_data_int32() {
        return bool_data_int32;
    }

    public void setBool_data_int32(String bool_data_int32) {
        this.bool_data_int32 = bool_data_int32;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
}
