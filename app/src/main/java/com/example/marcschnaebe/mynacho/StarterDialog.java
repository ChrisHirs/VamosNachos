package com.example.marcschnaebe.mynacho;


import android.app.Dialog;
import android.view.View;
import android.widget.ImageView;


/**
 * Dialog class for starters
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class StarterDialog extends Dialog implements View.OnClickListener {
    private MapsActivity mapsActivity;

    /* -------  Attributes  ------ */

    private ImageView starterSalamucho;
    private ImageView starterCaraponcho;
    private ImageView starterBulbiatchos;

    /* -------  Constructor ------- */

    /**
     * Constructor
     *
     * @param activity activity where the dialog is shown on
     */
    public StarterDialog(MapsActivity activity){
        super(activity);
        this.mapsActivity = activity;

        setContentView(R.layout.starters_nachomon);

        starterSalamucho = (ImageView) findViewById(R.id.starter_salamuchos);
        starterCaraponcho = (ImageView) findViewById(R.id.starter_caraponcho);
        starterBulbiatchos = (ImageView) findViewById(R.id.starter_bulbiatchos);

        starterSalamucho.setOnClickListener(this);
        starterBulbiatchos.setOnClickListener(this);
        starterCaraponcho.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String chosenNacho = "";
        switch (view.getId()) {
            case R.id.starter_salamuchos:
                chosenNacho = "Salamuchos";
                break;
            case R.id.starter_caraponcho:
                chosenNacho = "Caraponcho";
                break;
            case R.id.starter_bulbiatchos:
                chosenNacho = "Bulbiatchos";
                break;
        }
        mapsActivity.chooseStarterNachos(chosenNacho);
        dismiss();
    }
}
