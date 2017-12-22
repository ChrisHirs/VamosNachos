package com.example.marcschnaebe.mynacho;

import com.google.android.gms.maps.model.LatLng;


/**
 * Item class containing attributes and getters/setters
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class Item implements Markable {

    /* -------  Attributes  ------ */

    public long createdTime;
    private LatLng position;
    private String name;
    private String type;
    private int upgradePoints;

    /* -------  Constructor ------- */

    /**
     * Constructor
     *
     * @param latitude latitude on google map
     * @param longitude longitude on google map
     * @param _name name of item
     * @param _upgradePoints points of item
     * @param _type type of item
     */
    public Item(double latitude, double longitude, String _name, int _upgradePoints, String _type) {

        createdTime = System.currentTimeMillis();
        position = new LatLng(latitude, longitude);
        name = _name;
        upgradePoints = _upgradePoints;
        type = _type;

    }

    /**
     * Constructor
     *
     * @param _position position on google map
     * @param _name name of item
     * @param _type type of item
     * @param _upgradePoints points of item
     */
    public Item (LatLng _position, String _name, String _type, int _upgradePoints) {

        createdTime = System.currentTimeMillis();
        position = _position;
        name = _name;
        upgradePoints = _upgradePoints;
        type = _type;

    }

    /* -------  Getter & Setter  ------ */

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

    public int getUpgradePoints() { return upgradePoints; }

    public void setUpgradePoints(int upgradePoints) {
        this.upgradePoints = upgradePoints;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
