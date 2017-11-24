package com.example.marcschnaebe.mynacho;

import android.util.Log;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Nachos {

    /* -------  Attributes  ------ */

    public long createdTime = 0L;
    private LatLng position;

    private String name;
    private String type;

    private int level;
    private int xpCurrent;
    private int xpMax;

    private int ap;
    private int hpCurrent;
    private int hpMax;

    private int hpBonus;
    private int apBonus;

    /* -------  Constructor ------- */

    public Nachos(double latitude, double longitude, String _name, String _type, int _hp, int _ap, int _level) {

        createdTime = System.currentTimeMillis();
        position = new LatLng(latitude, longitude);

        name = _name;
        type = _type;

        level = _level;
        xpCurrent = 0;
        xpMax = level * 10;

        ap = _ap + level + Util.randomInteger(-1, 1);
        hpMax = _hp + level + Util.randomInteger(-3, 3);
        hpCurrent = hpMax;

        hpBonus = 0;
        apBonus = 0;

    }

    public Nachos(LatLng _position, String _name, String _type, int _level, int _xpCurrent,
                  int _xpMax, int _ap, int _hpCurrent, int _hpMax, int _hpBonus, int _apBonus) {

        createdTime = System.currentTimeMillis();
        position = _position;

        name = _name;
        type = _type;

        level = _level;
        xpCurrent = _xpCurrent;
        xpMax = _xpMax;

        ap = _ap;
        hpCurrent = _hpCurrent;
        hpMax = _hpMax;

        hpBonus = _hpBonus;
        apBonus = _apBonus;
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

    public void healNachosToMax() {
        hpCurrent = hpMax;
    }

    public void addToCurrentHp(int hp){
        if((hpCurrent + hp) <= hpMax){
            hpCurrent += hp;
        }
        else{
            hpCurrent = hpMax;
        }
    }

    public void addToCurrentXp(int xp) {
        xpCurrent += xp;
        if (xpCurrent >= xpMax) {
            leveledUp();
        }
    }

    public void addDef(int upgrade) {
        hpCurrent += upgrade;
        hpMax += upgrade;
        hpBonus += 1;
    }

    public void addAttack(int upgrade) {
        ap += upgrade;
        apBonus += 1;
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

    public int getXpPercent() {
        double percent = (double) xpCurrent/ (double) xpMax * 100.;
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

    public int getHpBonus() { return hpBonus; }

    public void setHpBonus(int hpBonus) { this.hpBonus = hpBonus; }

    public int getApBonus() { return apBonus; }

    public void setApBonus(int apBonus) { this.apBonus = apBonus; }
}
