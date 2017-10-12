package com.example.marcschnaebe.mynacho;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Nachos {

    private LatLng position;
    private String name;
    private int pv;

    public Nachos(double latitude, double longitude, int _pv) {

        position = new LatLng(latitude, longitude);
        pv = _pv;
        name = "Pikachos";
    }

    public Nachos(LatLng _position, int _pv) {

        position = _position;
        pv = _pv;
        name = "Pikachos";

    }

    public int getPv() { return pv; }

    public void setPv(int pv) { this.pv = pv; }

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
}
