package com.example.marcschnaebe.mynacho;


import android.app.Dialog;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by marc.schnaebe on 08.12.2017.
 */

public class StarterDialog extends Dialog implements View.OnClickListener {
    private MapsActivity mapsActivity;

    private ImageView starterSalamucho;
    private ImageView starterCaraponcho;
    private ImageView starterBulbiatchos;

    public StarterDialog(MapsActivity a){
        super(a);
        this.mapsActivity = a;

        setContentView(R.layout.starters_nachomon);
        //setTitle("Choose a starter Nachomon");

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
