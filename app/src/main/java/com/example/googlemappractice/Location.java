package com.example.googlemappractice;

public class Location {
    String locationId;
    String userId;

    public Location(){}
    public Location(String locationId, String userId) {
        this.locationId = locationId;
        this.userId = userId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
