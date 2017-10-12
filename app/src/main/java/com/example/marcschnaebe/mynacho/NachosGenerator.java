package com.example.marcschnaebe.mynacho;

import com.example.marcschnaebe.util.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class NachosGenerator {

    public static HashMap<Integer, Marker> mapMarker = new HashMap<Integer, Marker>();
    public static ArrayList<Nachos> listNachos;
    private static Integer id = 0;

    public NachosGenerator(GoogleMap mMap) {

        listNachos = new ArrayList<Nachos>();

    }

    public static void addNewWildNachos(GoogleMap mMap, Marker myPositionMarker) {

        LatLng position = myPositionMarker.getPosition();
        double latitude = Util.randomDouble(position.latitude - 0.001, position.latitude + 0.001);
        double longitude = Util.randomDouble(position.longitude - 0.001, position.longitude + 0.001);

        Nachos newNachos = new Nachos(id, latitude, longitude);
        listNachos.add(newNachos);
        Marker mNachos = mMap.addMarker(new MarkerOptions()
                .position(newNachos.getPosition())
                .title(newNachos.getName()));
        mNachos.setTag(0);;

        mapMarker.put(id, mNachos);
        id++;

    }

}
