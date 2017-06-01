package com.example.rishabh.googlefusedapi;

/**
 * Created by rishabh on 1/6/17.
 */

public class CoordinatesToFirebase {

    private double latitude , longitude ;
    private String deviceId ;


    CoordinatesToFirebase() {

    }

    CoordinatesToFirebase ( double latitude , double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }



    public double getLongitude() {
        return longitude;
    }



    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
