package com.example.marcschnaebe.util;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.marcschnaebe.mynacho.MapsActivity;
import com.example.marcschnaebe.mynacho.R;

import java.util.Random;

import static com.example.marcschnaebe.mynacho.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;


/**
 * Utils class
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class Util {

    /* -------  Attributes  ------ */

    static Random random = new Random(System.nanoTime());

    /* -------  Methods ------- */

    /**
     * Random integer
     *
     * @param min minimum number
     * @param max maximum number
     * @return random number int
     */
    public static int randomInteger(int min, int max)
    {
        return random.nextInt(max + 1 - min) + min;
    }

    /**
     * Random double
     *
     * @param min minimum number
     * @param max maximum number
     * @return random number double
     */
    public static double randomDouble(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    /**
     * Creates a top snackbar
     *
     * @param text text to display
     * @param view view to display on
     */
    public static void showSnackBar (String text, View view) {
        //Get bottom layout width
        int layoutBottomWidth = view.findViewById(R.id.layout_bottom_menu).getWidth();

        //Create snackbar with TSnackbar library
        TSnackbar snackBar = TSnackbar.make(view, text, TSnackbar.LENGTH_LONG);

        //Snackbar width
        snackBar.setMaxWidth(layoutBottomWidth);

        //Snackbar color
        View sView = snackBar.getView();
        sView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));

        //Centered text
        TextView mainTextView = (TextView) (snackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        snackBar.show();
    }

    /**
     * Verify user's localisation access permissions and ask them otherwise
     *
     * @param activity activity permissions
     * @return boolean True if authorized permissions, otherwise False
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