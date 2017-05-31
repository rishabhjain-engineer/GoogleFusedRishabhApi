package com.example.rishabh.googlefusedapi;

/**
 * Created by rishabh on 31/5/17.
 */

public class Coordinate {
    private double latitude;
    private double longitude;

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

    public String toString (){
        return "Coordinates: " + latitude + ", " +  longitude;
    }
}
