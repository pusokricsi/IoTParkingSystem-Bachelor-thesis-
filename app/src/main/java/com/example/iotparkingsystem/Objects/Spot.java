package com.example.iotparkingsystem.Objects;

public class Spot {
    private String spotId;
    private Boolean spotStatus;

    public Spot() {
    }

    public Spot(String spotId, Boolean spotStatus) {
        this.spotId = spotId;
        this.spotStatus = spotStatus;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public Boolean getSpotStatus() {
        return spotStatus;
    }

    public void setSpotStatus(Boolean spotStatus) {
        this.spotStatus = spotStatus;
    }
}
