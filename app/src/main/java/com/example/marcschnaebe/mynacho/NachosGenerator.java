package com.example.marcschnaebe.mynacho;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.marcschnaebe.mynacho.R.drawable.mustaupicos;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class NachosGenerator {

    /* -------  Consts  ------ */

    public static long generationTimer = 0; //System.currentTimeMillis() + 5000L
    private static final Map<String, String> caraponchoMap = new HashMap<String, String>();
    private static final Map<String, String> salamuchosMap = new HashMap<String, String>();
    private static final Map<String, String> buritopsMap = new HashMap<String, String>();
    private static final Map<String, String> mustaupicosMap = new HashMap<String, String>();
    private static final Map<String, String> bulbiatchosMap = new HashMap<String, String>();
    private static final Map<String, String> maracachuMap = new HashMap<String, String>();

    /* -------  Attributes  ------ */

    public static ArrayList<Map> nachosList = new ArrayList<Map>();

    /* -------  Initialisateur statique ------ */

    static {
        caraponchoMap.put("name", "Caraponcho");
        caraponchoMap.put("type", "Water");
        caraponchoMap.put("health-points", "10");
        caraponchoMap.put("attack-points", "2");

        salamuchosMap.put("name", "Salamuchos");
        salamuchosMap.put("type", "Fire");
        salamuchosMap.put("health-points", "10");
        salamuchosMap.put("attack-points", "3");

        buritopsMap.put("name", "Buritops");
        buritopsMap.put("type", "Rock");
        buritopsMap.put("health-points", "10");
        buritopsMap.put("attack-points", "1");

        mustaupicosMap.put("name", "Mustaupicos");
        mustaupicosMap.put("type", "Ground");
        mustaupicosMap.put("health-points", "10");
        mustaupicosMap.put("attack-points", "1");

        bulbiatchosMap.put("name", "Bulbiatchos");
        bulbiatchosMap.put("type", "Grass");
        bulbiatchosMap.put("health-points", "10");
        bulbiatchosMap.put("attack-points", "2");

        maracachuMap.put("name", "Maracachu");
        maracachuMap.put("type", "Electric");
        maracachuMap.put("health-points", "10");
        maracachuMap.put("attack-points", "3");

        nachosList.add(caraponchoMap);
        nachosList.add(salamuchosMap);
        nachosList.add(buritopsMap);
        nachosList.add(mustaupicosMap);
        nachosList.add(bulbiatchosMap);
        nachosList.add(maracachuMap);
    }

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
        String name = "";
        String type = "";
        int hp = 0;
        int ap = 0;

        for (Map<String, String> list : nachosList) {
            if (list.get("name").equals(stringNachos)) {
                //Attributs
                name = list.get("name");
                type = list.get("type");
                hp = Integer.parseInt(list.get("health-points"));
                ap = Integer.parseInt(list.get("attack-points"));
            }
        }

        return new Nachos(0.0, 0.0, name, type, hp, ap, 1);
    }

}
