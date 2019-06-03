package com.manager.nacelle_rent.entity;

public class SetUpData {
    private String device_id;
    private double overweight_current;
    private int angle_upperlimit;
    private int angle_lowerlimit;
    private int interval_snapshot;
    private int state;

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String deviceId) {
        this.device_id = deviceId;
    }

    public double getOverweight_current() {
        return overweight_current;
    }

    public void setOverweight_current(double overweight_current) {
        this.overweight_current = overweight_current;
    }

    public int getAngle_upperlimit() {
        return angle_upperlimit;
    }

    public void setAngle_upperlimit(int angle_upperlimit) {
        this.angle_upperlimit = angle_upperlimit;
    }

    public int getAngle_lowerlimit() {
        return angle_lowerlimit;
    }

    public void setAngle_lowerlimit(int angle_lowerlimit) {
        this.angle_lowerlimit = angle_lowerlimit;
    }

    public int getInterval_snapshot() {return interval_snapshot;}

    public void setInterval_snapshot(int interval_snapshot) {
        this.interval_snapshot = interval_snapshot;
    }

    public int getState() {return state;}

    public void setState(int state) {
        this.state = state;
    }

}
