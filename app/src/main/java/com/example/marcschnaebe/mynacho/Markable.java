package com.example.marcschnaebe.mynacho;

import com.google.android.gms.maps.model.LatLng;


/**
 * Interface for markable object
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public interface Markable {

    long createdTime = System.currentTimeMillis();

    String getName();
    LatLng getPosition();
    String getType();

}
