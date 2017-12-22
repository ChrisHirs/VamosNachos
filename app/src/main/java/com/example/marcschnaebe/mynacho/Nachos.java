package com.example.marcschnaebe.mynacho;

import android.util.Log;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.model.LatLng;


/**
 * Nachos class containing attributes and getters/setters
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
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

    /**
     * Constructor
     *
     * @param latitude latitude on google map
     * @param longitude longitude on google map
     * @param _name name of nachos
     * @param _type type of nachos
     * @param _hp health points of nachos
     * @param _ap attack points of nachos
     * @param _level level of nachos
     */
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

    /**
     * Constructor
     *
     * @param _position position on google map
     * @param _name name of nachos
     * @param _type type of nachos
     * @param _level level of nachos
     * @param _xpCurrent current experience of nachos
     * @param _xpMax maximum experience of nachos
     * @param _ap attack points of nachos
     * @param _hpCurrent current health points of nachos
     * @param _hpMax maximum health points of nachos
     * @param _hpBonus health points bonus
     * @param _apBonus attack points bonus
     */
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

    /**
     * Modifies attributes when nachos has leveled up
     */
    public void leveledUp () {

        level++;
        xpCurrent = 0;
        xpMax = level * 10;

        ap += 2;
        hpMax += 5;
        hpCurrent = hpMax;

    }

    /**
     * Heals nachos to maximum health
     */
    public void healNachosToMax() {
        hpCurrent = hpMax;
    }

    /**
     * Adds health points to current health points
     *
     * @param hp health points to add
     */
    public void addToCurrentHp(int hp){
        if((hpCurrent + hp) <= hpMax){
            hpCurrent += hp;
        }
        else{
            hpCurrent = hpMax;
        }
    }

    /**
     * Adds experience points to current experience points
     *
     * @param xp experience points to add
     * @return True if nachos has leveled up, otherwise False boolean
     */
    public boolean addToCurrentXp(int xp) {
        boolean hasLeveledUp = false;
        xpCurrent += xp;
        if (xpCurrent >= xpMax) {
            int xpOverflow = xpCurrent - xpMax;
            leveledUp();
            if(xpOverflow > 0){
                this.addToCurrentXp(xpOverflow);
            }

            hasLeveledUp = true;
        }

        return hasLeveledUp;
    }

    /**
     * Adds defensive points to current defensive points
     *
     * @param upgrade upgrading points to add
     */
    public void addDef(int upgrade) {
        hpCurrent += upgrade;
        hpMax += upgrade;
        hpBonus += 1;
    }

    /**
     * Adds offensive points to current offensive points
     *
     * @param upgrade upgrading points to add
     */
    public void addAttack(int upgrade) {
        ap += upgrade;
        apBonus += 1;
    }

    /**
     * Gives experience points based on enemy killed
     *
     * @param enemy nachos killed
     * @return potential experience points int
     */
    public int calcWinnableExperience(Nachos enemy){
        int potentialXP = 5;

        float diffLevel = (enemy.getLevel() - this.level);

        //Less XP
        if(diffLevel < 0){
            diffLevel = 1/((-diffLevel)+1);
        }
        //More XP
        else if (diffLevel > 0){
            diffLevel += 1;
        }
        //Same XP
        else{
            diffLevel = 1;
        }

        potentialXP = (int)(potentialXP * diffLevel);
        return potentialXP;
    }

    /**
     * Tells if a nachos won or loose against enemy nachos
     *
     * @param enemy nachos to kill
     * @return True if nachos won, otherwise False boolean
     */
    public boolean fightToDeathWith (Nachos enemy) {
        do {
            enemy.setHpCurrent(enemy.getHpCurrent() - ap);
            hpCurrent -= enemy.getAp();
        } while ( (hpCurrent > 0) && (enemy.getHpCurrent() > 0) );

        //Log
        Log.d("Combat", name + "(" + ap + ")" + " LVL: " + level + " PV: " + hpCurrent + "/" +
                hpMax + " XP: " + xpCurrent + "/" + xpMax + " -vs- " +
                enemy.getName() + "(" + enemy.getAp() + ")" + " LVL: " + enemy.getLevel() +
                " PV: " + enemy.getHpCurrent() + "/" + enemy.getHpMax() + " XP: " +
                enemy.getXpCurrent() + "/" + enemy.getXpMax());

        //Victory
        if (hpCurrent <= 0) {
            return false;
        }

        //Defeat
        else {
            return true;
        }
    }

    /* -------  Getter & Setter  ------ */

    public int getHpPercent() {
        double percent = (double) hpCurrent/ (double) hpMax * 100.;
        return (int) percent;
    }

    public int getXpPercent() {
        double percent = (double) xpCurrent/ (double) xpMax * 100.;
        return (int) percent;
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
