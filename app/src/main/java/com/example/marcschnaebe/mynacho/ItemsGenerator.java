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

    private static final Map<String, String> nachoPotionMap = new HashMap<String, String>(){{ put("name", "Nachopotion"); put("points", "5"); put("type", "Health"); }};
    private static final Map<String, String> nachoDefMap = new HashMap<String, String>(){{ put("name", "UpgradeDEF"); put("points", "5"); put("type", "Def"); }};
    private static final Map<String, String> upgradeFireMap = new HashMap<String, String>(){{ put("name", "UpgradeFire"); put("points", "2"); put("type", "Fire"); }};
    private static final Map<String, String> upgradeRockMap = new HashMap<String, String>(){{ put("name", "UpgradeRock"); put("points", "2");  put("type", "Rock");}};
    private static final Map<String, String> upgradeElectricMap = new HashMap<String, String>(){{ put("name", "UpgradeElectric"); put("points", "2"); put("type", "Electric"); }};
    private static final Map<String, String> upgradeGrassMap = new HashMap<String, String>(){{ put("name", "UpgradeGrass"); put("points", "2");  put("type", "Grass"); }};
    private static final Map<String, String> upgradeGroundMap = new HashMap<String, String>(){{ put("name", "UpgradeGround"); put("points", "2");  put("type", "Ground"); }};
    private static final Map<String, String> upgradeWaterMap = new HashMap<String, String>(){{ put("name", "UpgradeWater"); put("points", "2"); put("type", "Water"); }};

    public static ArrayList<Map> itemList = new ArrayList<Map>(){{
        add(nachoPotionMap);
        add(nachoDefMap);
        add(upgradeFireMap);
        add(upgradeRockMap);
        add(upgradeElectricMap);
        add(upgradeGrassMap);
        add(upgradeGroundMap);
        add(upgradeWaterMap);
    }};

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
