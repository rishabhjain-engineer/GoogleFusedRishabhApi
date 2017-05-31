package com.example.rishabh.googlefusedapi;

import android.graphics.Color;

/**
 * Created by rishabh on 31/5/17.
 */

public class Device_Coordinate_Model {

    private String deviceId ;
    private double latitude , longitude ;
    private String color;                         // deviceID color code for marker ;
                                                  // deviceId: 1-- RED.
                                                  //deviceId: 2 -- yellow
                                                  //deviceId:3 -- Green




    Device_Coordinate_Model( String deviceId){
        this.deviceId = deviceId ;
    }

    public void setDeviceId(String deviceId){
        this.deviceId = deviceId ;
    }

    public String getDeviceId(){
        return deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setColor(String c){
        color = c ;


    }

    public String getColor(){
        return color;
    }


}
