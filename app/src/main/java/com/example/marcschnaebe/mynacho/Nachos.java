package com.example.marcschnaebe.mynacho;

import android.util.Log;
import android.widget.Toast;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

import static android.R.attr.level;
import static com.example.marcschnaebe.mynacho.R.id.map;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Nachos {

    /* -------  Attributes  ------ */

    private LatLng position;

    private String name;
    private String type;

    private int level;
    private int xpCurrent;
    private int xpMax;

    private int ap;
    private int hpCurrent;
    private int hpMax;

    /* -------  Constructor ------- */

    public Nachos(double latitude, double longitude, String _name, String _type, int _hp, int _ap, int _level) {

        position = new LatLng(latitude, longitude);

        name = _name;
        type = _type;

        level = _level;
        xpCurrent = 0;
        xpMax = level * 10;

        ap = _ap + level + Util.randomInteger(-1, 1);
        hpMax = _hp + level + Util.randomInteger(-3, 3);
        hpCurrent = hpMax;

    }

    /* -------  Methods ------- */

    public void leveledUp () {

        level++;
        xpCurrent = 0;
        xpMax = level * 10;

        ap += 2;
        hpMax += 5;
        hpCurrent = hpMax;

    }

    public void addToCurrentHp(int hp){
        if((hpCurrent+hp) <= hpMax){
            hpCurrent+=hp;
        }
        else{
            hpCurrent=hpMax;
        }
    }

    public void addToCurrentXp(int xp) {
        xpCurrent += xp;
        if (xpCurrent >= xpMax) {
            leveledUp();
        }
    }

    public boolean isWinner (Nachos enemy) {
        do {
            enemy.setHpCurrent(enemy.getHpCurrent() - ap);
            hpCurrent -= enemy.getAp();
        } while ( (hpCurrent > 0) && (enemy.getHpCurrent() > 0) );

        //Du lourd
        Log.d("Combat", name + "(" + ap + ")" + " LVL: " + level + " PV: " + hpCurrent + "/" +
                hpMax + " XP: " + xpCurrent + "/" + xpMax + " -vs- " +
                enemy.getName() + "(" + enemy.getAp() + ")" + " LVL: " + enemy.getLevel() +
                " PV: " + enemy.getHpCurrent() + "/" + enemy.getHpMax() + " XP: " +
                enemy.getXpCurrent() + "/" + enemy.getXpMax());

        if (hpCurrent <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public int getHpPercent() {
        double percent = (double) hpCurrent/ (double) hpMax * 100.;
        return (int) percent;
    }

    public String getPositionToString() { return "Latitude: " + position.latitude + " Longitude: " + position.longitude; }

    /* -------  Getter & Setter  ------ */

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

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

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
