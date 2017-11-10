package com.example.marcschnaebe.util;


import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    public static int randomInteger(int min, int max)
    {
        Random random = new Random(System.nanoTime());
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
        Random random = new Random(System.nanoTime());
        return min + (max - min) * random.nextDouble();
    }

    public static void showSnackBar (String text, View view) {
        //Obtention de la taille du layout du bas
        int layoutBottomHeight = view.findViewById(R.id.layout_bottom_menu).getHeight();
        int layoutBottomWidth = view.findViewById(R.id.layout_bottom_menu).getWidth();

        //Création du snackbar
        Snackbar snackBar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);

        //Largeur du snackbar
        View sView = snackBar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sView.getLayoutParams();
        params.width = (int) layoutBottomWidth/3*2;
        params.gravity = (Gravity.CENTER | Gravity.BOTTOM);
        sView.setLayoutParams(params);

        //Couleur du snackbar
        sView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));

        //Texte centré
        TextView mainTextView = (TextView) (snackBar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        else
            mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        snackBar.show();
    }

}