package com.example.marcschnaebe.mynacho;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marc.schnaebe on 21.10.2017.
 */

public class ItemsGenerator {

    /* -------  Consts  ------ */

    private static final Map<String, String> nachoPotionMap = new HashMap<String, String>();
    private static final Map<String, String> nachoDefMap = new HashMap<String, String>();
    private static final Map<String, String> upgradeFireMap = new HashMap<String, String>();
    private static final Map<String, String> upgradeRockMap = new HashMap<String, String>();
    private static final Map<String, String> upgradeElectricMap = new HashMap<String, String>();
    private static final Map<String, String> upgradeGrassMap = new HashMap<String, String>();
    private static final Map<String, String> upgradeGroundMap = new HashMap<String, String>();
    private static final Map<String, String> upgradeWaterMap = new HashMap<String, String>();

    /* -------  Attributes  ------ */

    public static ArrayList<Map> itemList = new ArrayList<Map>();

    /* -------  Initialisateur statique ------ */

    static {
        nachoPotionMap.put("name", "Nachopotion");
        nachoPotionMap.put("points", "0");
        nachoPotionMap.put("type", "Health");

        nachoDefMap.put("name", "UpgradeDEF");
        nachoDefMap.put("points", "5");
        nachoDefMap.put("type", "Def");

        upgradeFireMap.put("name", "UpgradeFire");
        upgradeFireMap.put("points", "2");
        upgradeFireMap.put("type", "Fire");

        upgradeRockMap.put("name", "UpgradeRock");
        upgradeRockMap.put("points", "2");
        upgradeRockMap.put("type", "Rock");

        upgradeElectricMap.put("name", "UpgradeElectric");
        upgradeElectricMap.put("points", "2");
        upgradeElectricMap.put("type", "Electric");

        upgradeGrassMap.put("name", "UpgradeGrass");
        upgradeGrassMap.put("points", "2");
        upgradeGrassMap.put("type", "Grass");

        upgradeGroundMap.put("name", "UpgradeGround");
        upgradeGroundMap.put("points", "2");
        upgradeGroundMap.put("type", "Ground");

        upgradeWaterMap.put("name", "UpgradeWater");
        upgradeWaterMap.put("points", "2");
        upgradeWaterMap.put("type", "Water");

        itemList.add(nachoPotionMap);
        itemList.add(nachoDefMap);
        itemList.add(upgradeFireMap);
        itemList.add(upgradeRockMap);
        itemList.add(upgradeElectricMap);
        itemList.add(upgradeGrassMap);
        itemList.add(upgradeGroundMap);
        itemList.add(upgradeWaterMap);
    }

    /**
     * Creates a new Nachos.
     *
     * @param myPositionMarker Marqueur de la position du joueur
     * @return new Item
     */
    public static Item addNewItem(Marker myPositionMarker) {

        //On récupère une liste d'un item au hasard
        Map<String, String> mapItems = itemList.get(Util.randomInteger(0, itemList.size()-1));

        double latitude = 0;
        double longitude = 0;

        //On choisit une position au hasard d'après la position du joueur
        if(myPositionMarker != null){
            LatLng position = myPositionMarker.getPosition();
            latitude = Util.randomDouble(position.latitude - 0.0015, position.latitude + 0.0015);
            longitude = Util.randomDouble(position.longitude - 0.0015, position.longitude + 0.0015);
        }

        //Attributs
        String name = mapItems.get("name");
        String type = mapItems.get("type");
        int upgradePoints = Integer.parseInt(mapItems.get("points"));

        return new Item(latitude, longitude, name, upgradePoints, type);
    }
}
