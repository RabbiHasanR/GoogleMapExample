package com.example.googlemappractice;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {
    private GeoPoint geoPoint;
    private @ServerTimestamp Date timesTamp;
    private String userId;

    public UserLocation(){}
    public UserLocation(GeoPoint geoPoint, Date timesTamp, String userId) {
        this.geoPoint = geoPoint;
        this.timesTamp = timesTamp;
        this.userId = userId;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimesTamp() {
        return timesTamp;
    }

    public void setTimesTamp(Date timesTamp) {
        this.timesTamp = timesTamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
