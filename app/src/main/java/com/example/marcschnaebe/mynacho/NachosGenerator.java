package com.example.marcschnaebe.mynacho;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.marcschnaebe.mynacho.R.id.map;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class NachosGenerator {

    // public static HashMap<String, Map> nachosMap = new HashMap<String, Map>();
    public static ArrayList<Map> nachosList = new ArrayList<>();

    private static final Map<String, String> caraponchoMap = new HashMap<String, String>(){{ put("name", "Caraponcho"); put("type", "Water"); put("health-points", "10"); put("attack-points", "2"); }};
    private static final Map<String, String> salamuchosMap = new HashMap<String, String>(){{ put("name", "Salamuchos"); put("type", "Fire"); put("health-points", "10"); put("attack-points", "3"); }};
    private static final Map<String, String> buritopsMap = new HashMap<String, String>(){{ put("name", "Buritops"); put("type", "Rock"); put("health-points", "5"); put("attack-points", "1"); }};
    private static final Map<String, String> mustaupicosMap = new HashMap<String, String>(){{ put("name", "Mustaupicos"); put("type", "Ground"); put("health-points", "5"); put("attack-points", "1"); }};
    private static final Map<String, String> bulbiatchosMap = new HashMap<String, String>(){{ put("name", "Bulbiatchos"); put("type", "Grass"); put("health-points", "10"); put("attack-points", "2"); }};
    private static final Map<String, String> maracachuMap = new HashMap<String, String>(){{ put("name", "Maracachu"); put("type", "Electric"); put("health-points", "10"); put("attack-points", "3"); }};

    public NachosGenerator(GoogleMap mMap) {

        nachosList.add(caraponchoMap);
        nachosList.add(salamuchosMap);
        nachosList.add(buritopsMap);
        nachosList.add(mustaupicosMap);
        nachosList.add(bulbiatchosMap);
        nachosList.add(maracachuMap);

    }

    /**
     * Creates a new Nachos.
     *
     * @param myPositionMarker Marqueur de la position du joueur
     * @return new Nachos
     */
    public static Nachos addNewWildNachos(Marker myPositionMarker) {

        //On récupère une liste d'un nachos au hasard
        Map<String, String> mapNachos = nachosList.get(Util.randomInteger(0, nachosList.size()-1));

        //On choisit une position au hasard d'après la position du joueur
        LatLng position = myPositionMarker.getPosition();
        double latitude = Util.randomDouble(position.latitude - 0.0015, position.latitude + 0.0015);
        double longitude = Util.randomDouble(position.longitude - 0.0015, position.longitude + 0.0015);

        //Attributs
        String name = mapNachos.get("name");
        String type = mapNachos.get("type");
        int hp = Integer.parseInt(mapNachos.get("health-points"));
        int ap = Integer.parseInt(mapNachos.get("attack-points"));

        return new Nachos(latitude, longitude, name, type, hp, ap);

    }

}
