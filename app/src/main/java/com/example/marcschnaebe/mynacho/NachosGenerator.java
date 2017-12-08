package com.example.marcschnaebe.mynacho;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class NachosGenerator {

    /* -------  Consts  ------ */

    public static long generationTimer = 0; //System.currentTimeMillis() + 5000L
    private static final Map<String, String> caraponchoMap = new HashMap<String, String>(){{ put("name", "Caraponcho"); put("type", "Water"); put("health-points", "10"); put("attack-points", "2"); }};
    private static final Map<String, String> salamuchosMap = new HashMap<String, String>(){{ put("name", "Salamuchos"); put("type", "Fire"); put("health-points", "10"); put("attack-points", "3"); }};
    private static final Map<String, String> buritopsMap = new HashMap<String, String>(){{ put("name", "Buritops"); put("type", "Rock"); put("health-points", "10"); put("attack-points", "1"); }};
    private static final Map<String, String> mustaupicosMap = new HashMap<String, String>(){{ put("name", "Mustaupicos"); put("type", "Ground"); put("health-points", "10"); put("attack-points", "1"); }};
    private static final Map<String, String> bulbiatchosMap = new HashMap<String, String>(){{ put("name", "Bulbiatchos"); put("type", "Grass"); put("health-points", "10"); put("attack-points", "2"); }};
    private static final Map<String, String> maracachuMap = new HashMap<String, String>(){{ put("name", "Maracachu"); put("type", "Electric"); put("health-points", "10"); put("attack-points", "3"); }};

    /* -------  Attributes  ------ */

    public static ArrayList<Map> nachosList = new ArrayList<Map>(){{
        add(caraponchoMap);
        add(salamuchosMap);
        add(buritopsMap);
        add(mustaupicosMap);
        add(bulbiatchosMap);
        add(maracachuMap);
    }};

    /* -------  Methods ------- */

    /**
     * Creates a new Nachos.
     *
     * @param myPositionMarker Marqueur de la position du joueur
     * @param meanLevel Niveau moyen de l'équipe
     * @return new Nachos
     */
    public static Nachos addNewWildNachos(Marker myPositionMarker, double meanLevel) {

        //On récupère une liste d'un nachos au hasard
        Map<String, String> mapNachos = nachosList.get(Util.randomInteger(0, nachosList.size()-1));

        double latitude = 0;
        double longitude = 0;

        //On choisit une position au hasard d'après la position du joueur
        if(myPositionMarker != null){
            LatLng position = myPositionMarker.getPosition();
            latitude = Util.randomDouble(position.latitude - 0.0015, position.latitude + 0.0015);
            longitude = Util.randomDouble(position.longitude - 0.0015, position.longitude + 0.0015);
        }

        //Niveau d'après les Nachomons déjà présents dans l'équipe
        int level = (int) meanLevel + Util.randomInteger(-2, 2);
        level = (level > 0) ? level : 1;

        //Attributs
        String name = mapNachos.get("name");
        String type = mapNachos.get("type");
        int hp = Integer.parseInt(mapNachos.get("health-points"));
        int ap = Integer.parseInt(mapNachos.get("attack-points"));

        //Timer avant la prochaine génération
        long millis = (long) Util.randomInteger(5, 8) * 1000; //Entre 25 sec. à 45 sec.
        generationTimer = System.currentTimeMillis() + millis;

        return new Nachos(latitude, longitude, name, type, hp, ap, level);

    }

    /**
     * Creates a new Nachos based on a strin.
     *
     * @param stringNachos Nom du Nachos à créer
     * @return new Nachos
     */
    public static Nachos AddNewSpecificNachos (String stringNachos) {
        Map<String, String> chosenList = new HashMap<String, String>();

        for (Map<String, String> list : nachosList) {
            if (list.get("name").equals(stringNachos)) {
                chosenList = list;
            }
        }

        //Attributs
        String name = chosenList.get("name");
        String type = chosenList.get("type");
        int hp = Integer.parseInt(chosenList.get("health-points"));
        int ap = Integer.parseInt(chosenList.get("attack-points"));

        return new Nachos(0.0, 0.0, name, type, hp, ap, 1);
    }

}
