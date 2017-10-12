package com.example.marcschnaebe.mynacho;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Nachos {

    private LatLng position;
    private String name;
    private Integer id;

    public Nachos(Integer id, double latitude, double longitude) {

        id = id;
        position = new LatLng(latitude, longitude);
        name = "Pikachos";
    }

    public Nachos(LatLng position) {

        position = position;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
