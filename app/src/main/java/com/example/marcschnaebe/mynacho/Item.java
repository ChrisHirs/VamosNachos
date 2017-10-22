package com.example.marcschnaebe.mynacho;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by marc.schnaebe on 21.10.2017.
 */

public class Item {
    private LatLng position;

    private String name;

    private int upgradePoints;

    public Item(double latitude, double longitude, String _name, int upgradePoints) {

        position = new LatLng(latitude, longitude);
        name = _name;
        this.upgradePoints = upgradePoints;

        //Util.randomInteger(-2, 2);
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
}
