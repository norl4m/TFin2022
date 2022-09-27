package com.marlon.apolo.tfinal2022.individualChat.model;

public class MessageLocationPoc extends  MessageCloudPoc{
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

    @Override
    public String toString() {
        return "MessageLocationPoc{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                "} " + super.toString();
    }
}
