package com.example.marcschnaebe.mynacho;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.marcschnaebe.mynacho.R.id.map;


public class MapsActivity extends FragmentActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationSource,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        SensorEventListener{

    //API
    private GoogleApiClient mGoogleApiClient;

    //Carte et positionnement
    private GoogleMap mMap;
    private LatLng latLng;
    private float currentBearing = 0;
    private long lastPlayerRotation = 0;

    //Listener de localisation
    private OnLocationChangedListener mMapLocationListener = null;

    //Marqueur du joueur
    private Marker myPositionMarker;

    //Marqueur de la cible
    private Marker targetMarker;

    //SENSORS
    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAcceleroSensor;
    private Sensor mOrientationSensor;

    //Calculs des sensors pour la boussole
    private float[] gravityData = new float[3];
    private float[] geomagneticData  = new float[3];
    private boolean hasGravityData = false;
    private boolean hasGeomagneticData = false;
    private double rotationInDegrees;

    //Boolean pour différencier la capture du deathmatch
    private boolean isCapturing = false;
    private boolean isDeathMatching = false;

    //Type de sensors utilisés pour la boussole (true = déprécié)
    private boolean depreciatedOrientation = false;

    //Code des Permissions
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //Joueur
    private Player player = new Player(myPositionMarker);

    //Interface

    //Items
    private Button buttonTake;
    private TextView textItemsInfo;
    private ImageView imageItemsInfo;
    private LinearLayout layoutItemsInfo;

    private TableLayout layoutBag;
    private ImageButton buttonBag;

    Item chosenItem=null;
    final private ArrayList<ImageButton> items = new ArrayList<>();
    private ArrayList<TableRow> row = new ArrayList<>();

    //Nachos
    private Button buttonDeath;
    private Button buttonCapture;
    private ImageButton buttonTeam;

    private TextView textInfo;
    private ImageView imageInfo;
    private LinearLayout layoutInfo;

    private ViewFlipper viewFlipper;

    private ArrayList<ImageButton> buttonListBottomTeamNachos = new ArrayList<>();

    private ArrayList<ProgressBar> progressBarListBottomTeamNachos = new ArrayList<>();

    private ArrayList<ImageButton> buttonListUpperTeamNachos = new ArrayList<>();

    private ArrayList<ProgressBar> progressBarListUpperTeamNachos = new ArrayList<>();

    //Items
    private HashMap<Marker, Item> mapMarkerItems = new HashMap<Marker, Item>();

    //Nachomons
    private HashMap<Marker, Nachos> mapMarker = new HashMap<Marker, Nachos>();

    //Paramètres pour la localisation
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(2000)
            .setFastestInterval(1000)
            .setSmallestDisplacement(0);



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("test", "OnCreate");

        // -------------------
        //      MAP / API
        // -------------------

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        //Préparation de la connection à Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // -------------------
        //       SENSORS
        // -------------------

        //Sensor Manager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Affectation des Sensors
        if(depreciatedOrientation){
            //Dépréciée
            mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        else{
            //Magnétomètre et Accéleromètre
            mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mAcceleroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Liste des Sensors disponibles
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // Affiche le nombre de Sensors disponibles
        Log.d("sensor", Integer.toString(msensorList.size()));

        // Création d'une liste des Sensors de l'appareil
        String sSensList = new String("");
        Sensor tmp;
        int x, i;
        for (i=0;i<msensorList.size();i++){
            tmp = msensorList.get(i);
            sSensList = " "+sSensList+tmp.getName() + "\n";
        }
        //Affichage de la liste s'il y a au moins un Sensor
        if (i>0){
            Log.d("sensor", sSensList);
        }

        // -------------------
        //      INTERFACE
        // -------------------

        //Items
        buttonTake =  (Button) findViewById(R.id.buttonTake);

        layoutItemsInfo = (LinearLayout) findViewById(R.id.layoutItemsInfo);
        layoutItemsInfo.setVisibility(LinearLayout.GONE);

        textItemsInfo = (TextView) findViewById(R.id.textItemsInfo);
        imageItemsInfo = (ImageView) findViewById(R.id.imageItemsInfo);

        layoutBag = (TableLayout) findViewById(R.id.layoutBag);
        buttonBag = (ImageButton) findViewById(R.id.imageButtonBag);

        //Nachos
        buttonCapture = (Button) findViewById(R.id.buttonCapture);
        buttonCapture.setEnabled(false);

        buttonDeath = (Button) findViewById(R.id.buttonDeath);
        buttonDeath.setEnabled(false);

        buttonTeam = (ImageButton) findViewById(R.id.imageButtonTeam);

        buttonBag = (ImageButton) findViewById(R.id.imageButtonBag);

        layoutInfo = (LinearLayout) findViewById(R.id.layoutInfo);
        layoutInfo.setVisibility(LinearLayout.GONE);

        textInfo = (TextView) findViewById(R.id.textInfo);
        imageInfo = (ImageView) findViewById(R.id.imageInfo);


        //Remplissage de la liste de boutons de l'équipe de Nachomons
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN1));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN2));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN3));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN4));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN5));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN6));

        buttonListUpperTeamNachos.add((ImageButton) findViewById(R.id.imageButtonTeamN1));
        buttonListUpperTeamNachos.add((ImageButton) findViewById(R.id.imageButtonTeamN2));
        buttonListUpperTeamNachos.add((ImageButton) findViewById(R.id.imageButtonTeamN3));
        buttonListUpperTeamNachos.add((ImageButton) findViewById(R.id.imageButtonTeamN4));
        buttonListUpperTeamNachos.add((ImageButton) findViewById(R.id.imageButtonTeamN5));
        buttonListUpperTeamNachos.add((ImageButton) findViewById(R.id.imageButtonTeamN6));

        //Remplissage de la liste de barre de vie de l'équipe de Nachomons
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN1));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN2));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN3));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN4));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN5));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN6));

        progressBarListUpperTeamNachos.add((ProgressBar) findViewById(R.id.progressBarTeamN1));
        progressBarListUpperTeamNachos.add((ProgressBar) findViewById(R.id.progressBarTeamN2));
        progressBarListUpperTeamNachos.add((ProgressBar) findViewById(R.id.progressBarTeamN3));
        progressBarListUpperTeamNachos.add((ProgressBar) findViewById(R.id.progressBarTeamN4));
        progressBarListUpperTeamNachos.add((ProgressBar) findViewById(R.id.progressBarTeamN5));
        progressBarListUpperTeamNachos.add((ProgressBar) findViewById(R.id.progressBarTeamN6));


        //Remplissage de l'équipe avec les Nachomons du joueur
        player.initTeam();
        viewFlipper = (ViewFlipper) findViewById(R.id.myViewFlipper);

        //Update des images button et barres de vie de l'équipe Nachomon dans l'affichage
        updateDisplayInfoTeam(buttonListBottomTeamNachos, progressBarListBottomTeamNachos);

        //Création du layout pour le bag
        final TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 550, getResources().getDisplayMetrics()), 1f);
        final TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 1f);

        buttonBag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int j=0;
                if(viewFlipper.getCurrentView() == findViewById(R.id.layout_bag)){
                    int itemId=0;
                    for(int l=0; l<10; l++){
                        for(int k=0; k<5; k++ ){
                            row.get(l).removeView(items.get(itemId));
                            itemId++;
                        }
                        layoutBag.removeView(row.get(l));
                    }
                    for (int k = 0; k<player.getMaxBagSize()/5;k++){
                        layoutBag.removeView(row.get(k));
                    }
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_main)));
                }
                else{
                    int itemId=0;
                    items.clear();

                    for(int l=0; l<10; l++){
                        row.add(new TableRow(v.getContext()));
                        for(int k=0; k<5; k++ ){
                            row.get(l).setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT));
                            items.add(new ImageButton(v.getContext()));
                            //items.get(itemId).setImageResource(getResources().getIdentifier("noitem", "drawable", getPackageName()));
                            row.get(l).addView(items.get(itemId));
                            itemId++;
                        }
                        row.get(l).setLayoutParams(rowParams);
                        layoutBag.addView(row.get(l));
                        layoutBag.setShrinkAllColumns(true);
                        layoutBag.setStretchAllColumns(true);
                        layoutBag.setLayoutParams(tableParams);
                        layoutBag.setBackgroundColor(Color.DKGRAY);
                    }
                    for(int i=0; i < player.bag.size(); i++){
                        items.get(i).setImageResource(getResources().getIdentifier(player.bag.get(i).getName().toLowerCase(), "drawable", getPackageName()));
                    }
                    for (j=0; j<items.size(); j++){
                        Log.d("bag"," bag size: " + player.bag.size() + " items size: " + items.size());
                        final int index = j;
                        ImageButton button = items.get(j);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("bag", "item: "+items.get(index).toString()+ " bag size: " + player.bag.size() + " items size: " + items.size());
                                if(player.bag.size()>0){
                                    chosenItem = player.bag.get(index);

                                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_main)));
                                    Toast.makeText(getApplicationContext(), "Choose a Nachomons to applie item!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    layoutBag.setVisibility(TableLayout.VISIBLE);
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_bag)));
                }
            }
        });

        buttonTake.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(player.bag.size() < Player.getMaxBagSize()){
                    player.bag.add(player.getItemsTarget());
                    targetMarker.remove();
                    mapMarkerItems.remove(targetMarker);
                    layoutItemsInfo.setVisibility(LinearLayout.GONE);
                }

                for(int i=0; i<player.bag.size(); i++){
                    Log.d("bag", player.bag.get(i).getItemToString());
                }
                Log.d("bag", Integer.toString(player.bag.size()) + " / " + Integer.toString(Player.getMaxBagSize()));
            }
        });


        buttonTeam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //viewFlipper.showNext();
                if(viewFlipper.getCurrentView() == findViewById(R.id.layout_team)){
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_main)));
                }
                else{
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_team)));
                    updateDisplayInfoTeam(buttonListUpperTeamNachos, progressBarListUpperTeamNachos);
                }

            }
        });

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(player.team.size() < Player.getMaxTeamSize()){
                    isDeathMatching = false;
                    isCapturing = true;
                    Toast.makeText(getApplicationContext(), "Choose a Nachomons to fight!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDeath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCapturing = false;
                isDeathMatching = true;
                Toast.makeText(getApplicationContext(), "Choose a Nachomons to fight!", Toast.LENGTH_SHORT).show();
            }
        });




        for (i = 0; i < buttonListBottomTeamNachos.size(); i++) {
            final int index = i;
            ImageButton button = buttonListBottomTeamNachos.get(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Capture
                    if (player.getTarget() != null && isCapturing) {
                        Nachos chosenNachos = player.team.get(index);
                        if (chosenNachos.isWinner(player.getTarget())) {
                            player.getTarget().setHpCurrent(0);
                            player.team.add(player.getTarget());
                        }
                        else {
                            player.team.remove(chosenNachos);
                        }
                        isCapturing = false;
                    }

                    //Deathmatch
                    if (player.getTarget() != null && isDeathMatching) {
                        Nachos chosenNachos = player.team.get(index);
                        if (chosenNachos.isWinner(player.getTarget())) {
                            chosenNachos.addToCurrentXp(5);
                        }
                        else {
                            player.team.remove(chosenNachos);
                        }
                        isDeathMatching = false;
                    }

                    //Items
                    if (chosenItem != null) {
                        if (chosenItem.getType() == "Health"){
                            player.team.get(index).addToCurrentHp(chosenItem.getUpgradePoints());
                            player.bag.remove(index);
                        }
                        else{
                            if(chosenItem.getType()==player.team.get(index).getType()){
                                player.team.get(index).setAp(chosenItem.getUpgradePoints());
                                Toast.makeText(getApplicationContext(), "set Ap "+chosenItem.getType(), Toast.LENGTH_SHORT).show();
                                player.bag.remove(chosenItem);
                            }
                        }

                        chosenItem = null;

                        //TODO: mettre dans methode pas dry utilisé plus haut aussi.
                        int itemId = 0;
                        for (int l = 0; l < 10; l++) {
                            for (int k = 0; k < 5; k++) {
                                row.get(l).removeView(items.get(itemId));
                                itemId++;
                            }
                            layoutBag.removeView(row.get(l));
                        }
                        for (int k = 0; k < player.getMaxBagSize() / 5; k++) {
                            layoutBag.removeView(row.get(k));
                        }
                        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_main)));
                    }

                    updateDisplayInfoTeam(buttonListBottomTeamNachos, progressBarListBottomTeamNachos);
                    player.setTarget(null);
                    targetMarker.remove();
                    mapMarker.remove(targetMarker);
                    layoutInfo.setVisibility(LinearLayout.GONE);
                }
            });
        }

        // -------------------
        //       NACHOS
        // -------------------

    }

    @Override
    protected void onResume() {
        Log.d("test", "OnResume");
        super.onResume();

        //Connexion à l'API
        mGoogleApiClient.connect();

        //Enregistrement des listener des Sensors
        if(depreciatedOrientation){
            //Utilisation dépréciée
            mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            //Nouvelle façon de travailler avec un Accéleromètre et un Magnétomètre
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAcceleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Déconnexion de l'API
        mGoogleApiClient.disconnect();
        //Arrêt des listener de Sensors
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {

        Log.d("test", "OnMapReady");

        //Configuration de la Map
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setLocationSource(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                targetMarker = marker;
                Nachos nachos = mapMarker.get(marker);
                player.setTarget(nachos);

                Item items = mapMarkerItems.get(marker);
                player.setItemsTarget(items);

                if (nachos != null && myPositionMarker != null) {
                    LatLng myPosition = myPositionMarker.getPosition();
                    LatLng nachosPosition = nachos.getPosition();

                    float[] results = new float[3];
                    double startLatitude = myPosition.latitude;
                    double startLongitude = myPosition.longitude;
                    double endLatitude = nachosPosition.latitude;
                    double endLongitude = nachosPosition.longitude;
                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);

                    //TODO : Mise à jour sur le déplacement onlocchange

                    layoutItemsInfo.setVisibility(LinearLayout.GONE);
                    layoutInfo.setVisibility(LinearLayout.VISIBLE);
                    textInfo.setText("Name: " + nachos.getName() + " Type: " + nachos.getType() + " HP: " + nachos.getHpCurrent() + "/" + nachos.getHpMax() + " LVL: " + nachos.getLevel());
                    imageInfo.setImageResource(getResources().getIdentifier(nachos.getName().toLowerCase(), "drawable", getPackageName()));
                    if (results[0] < 400) { // < 40
                        buttonDeath.setEnabled(true);
                        Log.d("test", Integer.toString(player.team.size()) + " / " + Integer.toString(Player.getMaxTeamSize()));
                        if(player.team.size() < Player.getMaxTeamSize()){
                            buttonCapture.setEnabled(true);
                        }
                        else{
                            buttonCapture.setEnabled(false);
                        }

                    }
                    else {
                        buttonDeath.setEnabled(false);
                        buttonCapture.setEnabled(false);
                    }
                }

                if (items != null && myPositionMarker != null) {
                    LatLng myPosition = myPositionMarker.getPosition();
                    LatLng itemsPosition = items.getPosition();

                    float[] results = new float[3];
                    double startLatitude = myPosition.latitude;
                    double startLongitude = myPosition.longitude;
                    double endLatitude = itemsPosition.latitude;
                    double endLongitude = itemsPosition.longitude;
                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);

                    //TODO : Mise à jour sur le déplacement onlocchange

                    layoutInfo.setVisibility(LinearLayout.GONE);
                    layoutItemsInfo.setVisibility(LinearLayout.VISIBLE);
                    textItemsInfo.setText("Nom: " + items.getName() + " Points: " + items.getUpgradePoints() + " Type: " + items.getType());
                    imageItemsInfo.setImageResource(getResources().getIdentifier(items.getName().toLowerCase(), "drawable", getPackageName()));
                    if (results[0] < 100) {
                        if(player.team.size() < Player.getMaxTeamSize()){
                            buttonTake.setEnabled(true);
                        }
                        else{
                            buttonTake.setEnabled(false);
                        }

                    }
                    else {
                        buttonTake.setEnabled(false);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d("test", "Connecté");

        //Vérifie les permissions d'accès à la localisatiion de l'utilisateur
        if (checkLocationPermission()) {

            Log.d("test", "Connecté - Permissions accordées");

            //Si les permissions sont respectées
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Active les mises à jour de la localisation de l'utilisateur
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, REQUEST, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("test", "Connexion suspendue");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("test", "Connexion échouée");
    }


    /**
     * Permet de vérifier les permissions d'accès à la localisation de l'utilisateur et de les demander si elles ne sont pas encore validées.
     * @return boolean True si les permissions sont accordées, sinon False
     */
    public boolean checkLocationPermission() {

        Log.d("test", "CheckLocationPermission");

        //Si les permissions ne sont pas encore accordées ...
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("test", "CheckLocationPermission - Passage compliqué");

            // Si une explication est requise...
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Log.d("test", "CheckLocationPermission - Doit donner infos");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Localisation required")
                        .setMessage("This application needs an access to your localisation to work properly.")
                        .setPositiveButton("OK patrick", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }
            //Si aucune explication n'est requise
            else {

                Log.d("test", "CheckLocationPermission - Demande de la permission");

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        //Si les permissions sont déjà accordées
        else {
            Log.d("test", "CheckLocationPermission - Passe direct");

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d("test", "OnRequestPersmissionResult");

        //Selon le code de requête reçu
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                Log.d("test", "OnRequestPersmissionResult - Request code localisation");

                // Si la permissions a été accordée
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("test", "OnRequestPersmissionResult - Accès autorisé");

                }
                // Permission refusée
                else {
                    Log.d("test", "OnRequestPersmissionResult - Accès refusé");

                }
                return;
            }
        }
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {

        //Récupération de la position du joueur
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //Met à jour la localisation dans le listener
        if (mMapLocationListener != null) {
            mMapLocationListener.onLocationChanged(location);
        }

        //Options du marqueur du joueur
        MarkerOptions markerOptions = new MarkerOptions();

        //Si le marqueur n'existe pas encore
        if(myPositionMarker==null) {

            //Déclaration des options du marqueur du joueur et création de celui-ci
            myPositionMarker = mMap.addMarker(
                    new MarkerOptions().flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pepito))
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(location.getLatitude(), location.getLongitude())));
        }
        //Mise à jour animée de la position du marqueur joueur
        else{
            Animation.animateMarker(latLng, myPositionMarker, false, mMap);
        }

        //Mise à jour animée de la camera pointant sur la google map
        CameraPosition cam = new CameraPosition.Builder().target(latLng).bearing(currentBearing).zoom(18f).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam));

        //Création de nouveaux Nachomons et d'Items
        Nachos newNachos = NachosGenerator.addNewWildNachos(myPositionMarker, player.getMeanLevelTeam());
        Marker mNachos = placeMarker(newNachos);
        mapMarker.put(mNachos, newNachos);

        Item newItem =  ItemsGenerator.addNewItem(myPositionMarker);
        Marker mItem = placeItemMarker(newItem);
        mapMarkerItems.put(mItem, newItem);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        //Deux mode de calibrage de la boussoles
        if(depreciatedOrientation){
            if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

                //Mises à jour du marqueur du joueur et de la camera
                updateRotationPlayerMarker(event.values[0], 1);
                updateRotationCamera(event.values[0], 10);
            }
        }
        else{
            //Selon le Sensor déclenché récupère ses données
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    System.arraycopy(event.values, 0, gravityData, 0, 3);
                    hasGravityData = true;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    System.arraycopy(event.values, 0, geomagneticData, 0, 3);
                    hasGeomagneticData = true;
                    break;
                default:
                    return;
            }

            //Si l'acceleromètre et le magnétomètre fournissent tous les deux des informations
            if (hasGravityData && hasGeomagneticData) {
                float identityMatrix[] = new float[9];
                float rotationMatrix[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, identityMatrix, gravityData, geomagneticData);

                if (success) {
                    float orientationMatrix[] = new float[3];
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotationMatrix);

                    //Récupération de l'azimuth permettant de faire fonctionner la boussole
                    SensorManager.getOrientation(rotationMatrix, orientationMatrix);
                    float rotationInRadians = orientationMatrix[0];
                    rotationInDegrees = (float)(Math.toDegrees(rotationInRadians)+360)%360;

                    Log.d("test", Double.toString(rotationInDegrees));

                    //Mises à jour du marqueur du joueur et de la camera
                    updateRotationPlayerMarker((float)rotationInDegrees, 30);
                    updateRotationCamera((float)rotationInDegrees, 10);

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Mise à jour de l'angle de la camera pointant sur la Google Map
     * @param bearing angle
     * @param precision différence minimum d'angle avant trigger
     */
    private void updateRotationCamera(float bearing, int precision) {

        //Si la map a été initialisée et qu'une positiona déjà été trouvée
        if(mMap != null && latLng != null){
            //Si l'angle est suffisement différent de celui actuel
            if(Math.abs(currentBearing - bearing) > precision){
                //Mise à jour de la camera avec animation
                CameraPosition currentPlace = new CameraPosition.Builder().target(latLng).bearing(bearing).zoom(18f).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
            }
        }
        //Sauvegarde du nouvel angle
        currentBearing = bearing;

    }

    /**
     * Mise à jour de l'angle du marqueur du joueur sur la Google Map
     * @param bearing angle
     * @param precision différence minimum d'angle avant trigger
     */
    private void updateRotationPlayerMarker(float bearing, int precision) {

        //Si le marqueur a déjà été initialisé et la différence avec l'angle actuel est assez grande
        if(myPositionMarker != null && Math.abs(myPositionMarker.getRotation()-bearing) > precision){
            //Si le temps écoulé depuis la dernière rotation est suffisant
            if(SystemClock.uptimeMillis() - lastPlayerRotation > 1000){
                //Mise à jour du marqueur avec animation
                Animation.rotateMarker(myPositionMarker, bearing);
                lastPlayerRotation = SystemClock.uptimeMillis();
            }
        }
    }

    /**
     * Place un nouveau marqueur pour l'item donné en paramètre sur la Google Map
     * @param item Item a placer sur la Map
     * @return Marker de l'Item donné
     */
    public Marker placeItemMarker(Item item) {
        Marker mItem = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(item.getName().toLowerCase(), "drawable", getPackageName())))
                .position(item.getPosition()));
        return mItem;
    }

    /**
     * Place un nouveau marqueur pour le Nachomon donné en paramètre sur la Google Map
     * @param nachos Nachomon a placer sur la Map
     * @return Marker du Nachomon donné
     */
    public Marker placeMarker(Nachos nachos) {
        Marker mNachos = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(nachos.getName().toLowerCase(), "drawable", getPackageName())))
                .position(nachos.getPosition()));
        return mNachos;
    }

    public void updateDisplayInfoTeam(ArrayList<ImageButton> buttonList, ArrayList<ProgressBar> progressBarList){
        for (int i = 0; i < Player.getMaxTeamSize(); i++) {
            if (i < player.team.size()) {
                buttonList.get(i).setImageResource(getResources().getIdentifier(player.team.get(i).getName().toLowerCase(), "drawable", getPackageName()));
                progressBarList.get(i).setProgress(player.team.get(i).getHpPercent());
                buttonList.get(i).setVisibility(View.VISIBLE);
                progressBarList.get(i).setVisibility(View.VISIBLE);
            }
            //Cache les emplacements vides de l'équipe
            else {
                buttonList.get(i).setVisibility(View.INVISIBLE);
                progressBarList.get(i).setVisibility(View.INVISIBLE);
            }

        }
    }

}
