package com.example.marcschnaebe.mynacho;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;


/**
 * Player class containing attributs and methods
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class Player {

    /* -------  Consts  ------ */

    private static int maxTeamSize = 6;
    private static int maxBagSize = 50;

    /* -------  Attributes  ------ */

    private Nachos target = null;
    private Item targetItem = null;
    private Marker marker;

    public ArrayList<Nachos> team = new ArrayList<Nachos>();
    public ArrayList<Item> bag = new ArrayList<Item>();

    /* -------  Constructor ------- */

    /**
     * Constructor
     *
     * @param myMarker player's marker
     */
     public Player(Marker myMarker){
         marker = myMarker;
     }

     /* -------  Methods ------- */

    /**
     * Returns mean level of existing nachos team
     *
     * @return mean level, otherwise 1 double
     */
    public double getMeanLevelTeam() {
        double sum = 0.;

        if (!team.isEmpty()) {
            for (Nachos nachos : team) {
                sum += nachos.getLevel();
            }
            return sum / (double) team.size();
        }

        return 1.;
    }

    /* -------  Getter & Setter  ------ */

    public static int getMaxTeamSize(){ return maxTeamSize;}
    public static int getMaxBagSize(){ return maxBagSize;}

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Nachos getTarget() {
        return target;
    }

    public void setTarget(Nachos target) { this.target = target; }

    public Item getItemsTarget() {
        return targetItem;
    }

    public void setItemsTarget(Item target) { this.targetItem = target; }
}
