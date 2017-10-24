package com.example.marcschnaebe.mynacho;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by marc.schnaebe on 21.10.2017.
 */

public class Item {
    private LatLng position;

    private String name;
    private String type;

    private int upgradePoints;

    public Item(double latitude, double longitude, String _name, int upgradePoints, String type) {

        position = new LatLng(latitude, longitude);
        name = _name;
        this.upgradePoints = upgradePoints;
        this.type=type;
    }


    public String getPositionToString() { return "Latitude: " + position.latitude + " Longitude: " + position.longitude; }

    public String getItemToString() { return "Name: " + name + " points: " + upgradePoints; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getUpgradePoints() {
        return upgradePoints;
    }

    public void setUpgradePoints(int upgradePoints) {
        this.upgradePoints = upgradePoints;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
