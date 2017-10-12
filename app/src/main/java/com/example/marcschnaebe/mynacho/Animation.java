package com.example.marcschnaebe.mynacho;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by anthony.fleury on 12.10.2017.
 */

//Found on : https://stackoverflow.com/questions/28967821/animate-the-rotation-of-the-marker-in-google-map-v2

public class Animation {

    private static boolean isMarkerRotating = false;

    public static void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating){
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;
            float deltaRotation = Math.abs(toRotation - startRotation) % 360;
            final float rotation = (deltaRotation > 180 ? 360 - deltaRotation : deltaRotation) * ((toRotation - startRotation >= 0 && toRotation - startRotation <= 180)
                    || (toRotation - startRotation <=-180 && toRotation- startRotation>= -360) ? 1 : -1);

            final LinearInterpolator interpolator = new LinearInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    marker.setRotation((startRotation + t* rotation)%360);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }else {
                        isMarkerRotating = false;
                    }
                }
            });
        }

    }


    //Found on : https://stackoverflow.com/questions/21403496/how-to-get-current-location-in-google-map-android

    public static void animateMarker(final LatLng toPosition, final Marker m, final boolean hideMarke, GoogleMap googleMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(m.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                m.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarke) {
                        m.setVisible(false);
                    } else {
                        m.setVisible(true);
                    }
                }
            }
        });
    }
}
