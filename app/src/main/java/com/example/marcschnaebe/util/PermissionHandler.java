package com.example.marcschnaebe.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static com.example.marcschnaebe.mynacho.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;


/**
 * Permissions handler class
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class PermissionHandler {

    /* -------  Methods ------- */

    /**
     * Verify user's localisation access permissions and ask them otherwise
     *
     * @param activity activity permissions
     * @return boolean True if authorized permissions, otherwise False boolean
     */
    public static boolean checkLocationPermission(Activity activity) {
        final Activity MyActivity = activity;

        Log.d("test", "CheckLocationPermission");

        //If they are not authorized...
        if (ContextCompat.checkSelfPermission(MyActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("test", "CheckLocationPermission - Passage compliqué");

            //If one permission is needed...
            if (ActivityCompat.shouldShowRequestPermissionRationale(MyActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Log.d("test", "CheckLocationPermission - Doit donner infos");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(MyActivity)
                        .setTitle("Localisation required")
                        .setMessage("This application needs an access to your localisation to work properly.")
                        .setPositiveButton("OK patrick", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MyActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }
            //If none is needed...
            else {

                Log.d("test", "CheckLocationPermission - Demande de la permission");

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MyActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }

        //If permissions are already authorized...
        else {
            Log.d("test", "CheckLocationPermission - Passe direct");
            return true;
        }
    }
}
