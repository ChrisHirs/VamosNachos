package com.example.marcschnaebe.util;


import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.marcschnaebe.mynacho.R;

import java.util.Random;

/**
 * Created by christop.hirschi on 11.10.2017.
 */

public class Util {

    /**
     * Integer aléatoire
     *
     * @param min nombre minimum
     * @param max nombre maximum
     * @return nombre aléatoire int
     */
    static Random random = new Random(System.nanoTime());

    public static int randomInteger(int min, int max)
    {
        return random.nextInt(max + 1 - min) + min;
    }

    /**
     * Double aléatoire
     *
     * @param min nombre minimum
     * @param max nombre maximum
     * @return nombre aléatoire double
     */
    public static double randomDouble(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    public static void showSnackBar (String text, View view) {
        //Obtention de la largeur du layout du bas
        int layoutBottomWidth = view.findViewById(R.id.layout_bottom_menu).getWidth();

        //Création du snackbar avec la librairie TSnackbar
        TSnackbar snackBar = TSnackbar.make(view, text, TSnackbar.LENGTH_LONG);

        //largeur du snackbar
        snackBar.setMaxWidth(layoutBottomWidth);

        //Couleur du snackbar
        View sView = snackBar.getView();
        sView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));

        //Texte centré
        TextView mainTextView = (TextView) (snackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        snackBar.show();
    }

}