package com.example.transitsync;


public class Bus {
    private int imageResId;
    private String routeName;
    private String busNumber;
    private String busTimings;
    private String distanceCovered;
    private String nearbyStops;

    public Bus(int imageResId, String routeName, String busNumber, String busTimings, String distanceCovered, String nearbyStops) {
        this.imageResId = imageResId;
        this.routeName = routeName;
        this.busNumber = busNumber;
        this.busTimings = busTimings;
        this.distanceCovered = distanceCovered;
        this.nearbyStops = nearbyStops;
    }

    public int getImageResId() { return imageResId; }
    public String getRouteName() { return routeName; }
    public String getBusNumber() { return busNumber; }
    public String getBusTimings() { return busTimings; }
    public String getDistanceCovered() { return distanceCovered; }
    public String getNearbyStops() { return nearbyStops; }
}