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
}