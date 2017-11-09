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

    public Item(double latitude, double longitude, String _name, int _upgradePoints, String _type) {

        position = new LatLng(latitude, longitude);
        name = _name;
        upgradePoints = _upgradePoints;
        type = _type;

    }

    public Item (LatLng _position, String _name, String _type, int _upgradePoints) {

        position = _position;
        name = _name;
        upgradePoints = _upgradePoints;
        type = _type;

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
