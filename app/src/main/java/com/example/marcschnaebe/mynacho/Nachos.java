package com.example.marcschnaebe.mynacho;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

import static com.example.marcschnaebe.mynacho.R.id.map;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Nachos {

    private LatLng position;

    private String name;
    private String type;

    private int lvl;
    private int xpCurrent;
    private int xpMax;

    private int ap;
    private int hpCurrent;
    private int hpMax;

    public Nachos(double latitude, double longitude, String _name, String _type, int _hp, int _ap) {

        position = new LatLng(latitude, longitude);

        name = _name;
        type = _type;

        //TODO: level sur le niveau moyen des Nachomons attrapés.
        lvl = 1;
        xpCurrent = 0;
        xpMax = lvl * 10;

        ap = _ap + Util.randomInteger(-2, 2);
        hpMax = _hp + Util.randomInteger(-5, 5);
        hpCurrent = hpMax;

    }

    public void leveledUp () {

        lvl++;
        xpCurrent = 0;
        xpMax = lvl * 10;

        ap = ap + 2;
        hpMax = hpMax + 5;
        hpCurrent = hpMax;

    }

    public int getHpPercent(){
        return (int)(this.getHpCurrent()/this.getHpMax()*100);
    }

    public String getPositionToString() { return "Latitude: " + position.latitude + " Longitude: " + position.longitude; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public int getLvl() { return lvl; }

    public void setLvl(int lvl) { this.lvl = lvl; }

    public int getXpCurrent() { return xpCurrent; }

    public void setXpCurrent(int xpCurrent) { this.xpCurrent = xpCurrent; }

    public int getXpMax() { return xpMax; }

    public void setXpMax(int xpMax) { this.xpMax = xpMax; }

    public int getAp() { return ap; }

    public void setAp(int ap) { this.ap = ap; }

    public int getHpCurrent() { return hpCurrent; }

    public void setHpCurrent(int hpCurrent) { this.hpCurrent = hpCurrent; }

    public int getHpMax() { return hpMax; }

    public void setHpMax(int hpMax) { this.hpMax = hpMax; }
}
