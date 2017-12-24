package com.example.marcschnaebe.mynacho;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.marcschnaebe.util.PermissionHandler;
import com.example.marcschnaebe.util.Util;
import com.example.marcschnaebe.util.XMLHandler;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.marcschnaebe.mynacho.R.id.map;


/**
 * Main activity containing stuff related to Google Map and whatnot
 *
 * @author Fleury Anthony, Hirschi Christophe, Schnaebele Marc
 * @version 12.2017
 */
public class MapsActivity extends FragmentActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationSource,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        SensorEventListener{

    /* -------  Attributes  ------ */

    //API
    private GoogleApiClient mGoogleApiClient;

    //Map and positions
    private GoogleMap mMap;
    private LatLng latLng;
    private float currentBearing = 0;
    private long lastPlayerRotation = 0;

    //Localisation listener
    private OnLocationChangedListener mMapLocationListener = null;

    //Player's marker
    private Marker myPositionMarker;

    //Target's marker
    private Marker targetMarker;

    //Sensors
    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAcceleroSensor;
    private Sensor mOrientationSensor;

    //Compass sensors calculations
    private float[] gravityData = new float[3];
    private float[] geomagneticData  = new float[3];
    private boolean hasGravityData = false;
    private boolean hasGeomagneticData = false;
    private double rotationInDegrees;

    //Deathmatch differentiation booleans
    private boolean isCapturing = false;
    private boolean isDeathMatching = false;

    //Compass used sensors types (true = depreciated)
    private boolean depreciatedOrientation = false;

    //Player
    private Player player = new Player(myPositionMarker);

    //Items
    private Button buttonTake;
    private TextView textItemsInfo;
    private ImageView imageItemsInfo;
    private LinearLayout layoutItemsInfo;

    private HashMap<Marker, Item> mapMarkerItems = new HashMap<Marker, Item>();

    private ImageButton buttonBag;

    private Item chosenItem = null;

    //Nachos
    private Button buttonDeath;
    private Button buttonCapture;
    private ImageButton buttonTeam;

    private TextView textInfo;
    private ImageView imageInfo;
    private LinearLayout layoutInfo;

    private ViewFlipper viewFlipper;
    private TeamAdapter teamAdapter;

    private GridView gridViewBag;
    private BagAdapter bagAdapter;

    private ArrayList<ImageButton> buttonListBottomTeamNachos = new ArrayList<>();
    private ArrayList<ProgressBar> progressBarListBottomTeamNachos = new ArrayList<>();

    private HashMap<Marker, Nachos> mapMarkerNachos = new HashMap<Marker, Nachos>();

    /* -------  Consts  ------ */

    //Minimal distance to catch a nachos or item
    private int minDist = 40 ;

    //Permission code
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //Localisation parameters
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(2000)
            .setFastestInterval(1000)
            .setSmallestDisplacement(0);

    /* -------  Methods ------- */

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

        //Google connection preparation
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

        //Sensors affectations
        if(depreciatedOrientation){
            //Depreciated
            mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        else{
            //Magnetometer et Accelerometer
            mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mAcceleroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        //Available sensors list
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        //Display number of available sensors
        Log.d("sensor", Integer.toString(msensorList.size()));

        //Creation of a list of sensors' device
        String sSensList = new String("");
        Sensor tmp;
        int x, i;
        for (i=0;i<msensorList.size();i++){
            tmp = msensorList.get(i);
            sSensList = " "+sSensList+tmp.getName() + "\n";
        }

        //Display a list if there is at least one sensor
        if (i>0){
            Log.d("sensor", sSensList);
        }

        // -------------------
        //      INTERFACE
        // -------------------

        //Items
        buttonTake = (Button) findViewById(R.id.buttonTake);

        layoutItemsInfo = (LinearLayout) findViewById(R.id.layoutItemsInfo);
        layoutItemsInfo.setVisibility(LinearLayout.GONE);

        textItemsInfo = (TextView) findViewById(R.id.textItemsInfo);
        imageItemsInfo = (ImageView) findViewById(R.id.imageItemsInfo);

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

        //Fill a list of Nachos team buttons
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN1));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN2));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN3));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN4));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN5));
        buttonListBottomTeamNachos.add((ImageButton) findViewById(R.id.imageButtonN6));

        //Fill a list of Nachos healthbars
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN1));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN2));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN3));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN4));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN5));
        progressBarListBottomTeamNachos.add((ProgressBar) findViewById(R.id.progressBarN6));

        //Get XML files containing items and team
        File file = new File(getApplicationContext().getFilesDir(), "objects");

        //Fill team with player's Nachos if file exists
        if (file.exists() && file.length() != 0) {
            XMLHandler.populateFromXMLFile(file, player);
        }

        //ViewFlipper
        viewFlipper = (ViewFlipper) findViewById(R.id.myViewFlipper);

        viewFlipper.setInAnimation(this, R.anim.view_transition_in_bottom);
        viewFlipper.setOutAnimation(this, R.anim.view_transition_out_bottom);

        //Update Nachos healthbars and buttons on display
        updateDisplayInfoTeam(buttonListBottomTeamNachos, progressBarListBottomTeamNachos);

        //bag button
        buttonBag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(viewFlipper.getCurrentView() == findViewById(R.id.layout_bag)){
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_main)));
                }
                else{
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

                    bagAdapter.notifyDataSetChanged();
                }

                for(int i=0; i<player.bag.size(); i++){
                    Log.d("bag", player.bag.get(i).getItemToString());
                }
                Log.d("bag", Integer.toString(player.bag.size()) + " / " + Integer.toString(Player.getMaxBagSize()));
            }
        });

        buttonTeam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(viewFlipper.getCurrentView() == findViewById(R.id.layout_team)){
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_main)));
                }
                else{
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(findViewById(R.id.layout_team)));
                }

            }
        });

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(player.team.size() < Player.getMaxTeamSize()){
                    isDeathMatching = false;
                    isCapturing = true;
                    Util.showSnackBar("Choose a Nachomon to fight it!", findViewById(android.R.id.content));
                }
            }
        });

        buttonDeath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCapturing = false;
                isDeathMatching = true;
                Util.showSnackBar("Choose a Nachomon to fight it!", findViewById(android.R.id.content));
            }
        });


        // ------------------------------
        //       TEAM BOTTOM BUTTONS HANDLING
        // ------------------------------

        for (i = 0; i < buttonListBottomTeamNachos.size(); i++) {
            final int index = i;
            ImageButton button = buttonListBottomTeamNachos.get(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Capture
                    if (player.getTargetNachos() != null && isCapturing) {
                        chosenItem = null;
                        Nachos chosenNachos = player.team.get(index);
                        if (chosenNachos.fightToDeathWith(player.getTargetNachos())) {
                            player.getTargetNachos().setHpCurrent(0);
                            player.team.add(player.getTargetNachos());
                        }
                        else {
                            player.team.remove(index);
                            showStarterDialog();
                        }
                        isCapturing = false;
                    }

                    //Deathmatch
                    if (player.getTargetNachos() != null && isDeathMatching) {
                        chosenItem = null;
                        Nachos chosenNachos = player.team.get(index);
                        if (chosenNachos.fightToDeathWith(player.getTargetNachos())) {
                            if (chosenNachos.addToCurrentXp(chosenNachos.calcWinnableExperience(player.getTargetNachos()))) {
                                Util.showSnackBar(chosenNachos.getName() + "'s leveled up!", findViewById(android.R.id.content));
                            }
                        }
                        else {
                            player.team.remove(index);
                            showStarterDialog();
                        }
                        isDeathMatching = false;
                    }

                    //Items
                    if (chosenItem != null) {
                        Nachos nacho = player.team.get(index);

                        if (chosenItem.getType().equals("Health")){
                            if (nacho.getHpCurrent() != nacho.getHpMax()) {
                                nacho.healNachosToMax();
                                player.bag.remove(chosenItem);
                                Util.showSnackBar(nacho.getName() + "'s health restored!", findViewById(android.R.id.content));
                            }
                            else {
                                Util.showSnackBar(nacho.getName() + " is already fine!", findViewById(android.R.id.content));
                            }
                        }
                        else if (chosenItem.getType().equals("Def")) {
                            nacho.addDef(chosenItem.getUpgradePoints());
                            player.bag.remove(chosenItem);
                            Util.showSnackBar(nacho.getName() + " is tougher!", findViewById(android.R.id.content));
                        }
                        else{
                            if(chosenItem.getType().equals(nacho.getType())){
                                nacho.addAttack(chosenItem.getUpgradePoints());
                                Log.d("Upgrade", "Added one " + player.team.get(index).getApBonus());
                                player.bag.remove(chosenItem);
                                Util.showSnackBar(nacho.getName() + " is stronger!", findViewById(android.R.id.content));
                            }
                            else {
                                Util.showSnackBar("This Nachomon type doesn't match the item!", findViewById(android.R.id.content));
                            }
                        }

                        chosenItem = null;
                    }

                    //Target deletion
                    if (player.getTargetNachos() != null) {
                        player.setTargetNachos(null);
                        targetMarker.remove();
                        mapMarkerNachos.remove(targetMarker);
                        layoutInfo.setVisibility(LinearLayout.GONE);
                    }

                    //Update team and bag
                    teamAdapter.notifyDataSetChanged();
                    bagAdapter.notifyDataSetChanged();

                    updateDisplayInfoTeam(buttonListBottomTeamNachos, progressBarListBottomTeamNachos);
                }
            });
        }

        // ------------------------------
        //       NACHOS TEAM LIST DISPLAY
        // ------------------------------

        ListView listViewTeam = (ListView) findViewById(R.id.layout_team);

        teamAdapter = new TeamAdapter(this, player.team);
        listViewTeam.setAdapter(teamAdapter);

        // ------------------------------
        //       BAG LIST DISPLAY
        // ------------------------------

        gridViewBag = (GridView) findViewById(R.id.layout_bag);

        bagAdapter = new BagAdapter(this, player.bag);
        gridViewBag.setAdapter(bagAdapter);

        gridViewBag.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("bag", "item: "+player.bag.get(i).toString()+ " bag size: " + player.bag.size() + " items size: " + player.bag.size());

                chosenItem = player.bag.get(i);
                isCapturing = isDeathMatching = false;
                player.setTargetNachos(null);

                Util.showSnackBar("Choose a Nachomons to apply it!", findViewById(android.R.id.content));

            }
        });

        showStarterDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //API Connection
        mGoogleApiClient.connect();

        //Save sensor listeners
        if(depreciatedOrientation){

            //Depreciated utilisation
            mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            //New way of using the Accelerometer and the Magnetometer
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAcceleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //Check if GPS is activated by user
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Display a popup in case it is deactivated
        if(!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your GPS is off");
            builder.setMessage("You need to turn on your GPS to enjoy the game!\n\nPlease activate it.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Pass
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //API Disconnection
        mGoogleApiClient.disconnect();

        //Stop sensors listeners
        mSensorManager.unregisterListener(this);

        //XML Writing
        XMLHandler.writeXMLFile(player, getApplicationContext());
    }

    @Override
    public void onMapReady(GoogleMap map) {

        Log.d("test", "OnMapReady");

        //Map Configuration
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setLocationSource(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //Hide marker's infos if user touches everywhere else on map
                layoutInfo.setVisibility(LinearLayout.GONE);
                layoutItemsInfo.setVisibility(LinearLayout.GONE);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                targetMarker = marker;

                LatLng myPosition = myPositionMarker.getPosition();
                LatLng objectPosition = marker.getPosition();

                float[] results = new float[3];
                double startLatitude = myPosition.latitude;
                double startLongitude = myPosition.longitude;
                double endLatitude = objectPosition.latitude;
                double endLongitude = objectPosition.longitude;
                Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);

                //TODO : Rather deactivate player's marker touchability and use one 'if / else' than a 'if / else if'
                Nachos nachos = mapMarkerNachos.get(marker);
                Item items = mapMarkerItems.get(marker);

                //Nachos
                if (nachos != null && myPositionMarker != null) {
                    player.setTargetNachos(nachos);

                    layoutItemsInfo.setVisibility(LinearLayout.GONE);
                    layoutInfo.setVisibility(LinearLayout.VISIBLE);
                    textInfo.setText("Name: " + nachos.getName() + " Type: " + nachos.getType() + " HP: " + nachos.getHpCurrent() + "/" + nachos.getHpMax() + " LVL: " + nachos.getLevel());
                    imageInfo.setImageResource(getResources().getIdentifier(nachos.getName().toLowerCase(), "drawable", getPackageName()));
                    if (results[0] < minDist) { //400
                        buttonDeath.setEnabled(true);
                        Log.d("test", Integer.toString(player.team.size()) + " / " + Integer.toString(Player.getMaxTeamSize()));
                        if (player.team.size() < Player.getMaxTeamSize()) {
                            buttonCapture.setEnabled(true);
                        } else {
                            buttonCapture.setEnabled(false);
                        }

                    } else {
                        buttonDeath.setEnabled(false);
                        buttonCapture.setEnabled(false);
                    }
                }

                //Items
                else if (items != null && myPositionMarker != null) {
                    player.setItemsTarget(items);

                    layoutItemsInfo.setVisibility(LinearLayout.VISIBLE);
                    layoutInfo.setVisibility(LinearLayout.GONE);
                    textItemsInfo.setText("Nom: " + items.getName() + " Type: " + items.getType());
                    imageItemsInfo.setImageResource(getResources().getIdentifier(items.getName().toLowerCase(), "drawable", getPackageName()));

                    if (results[0] < minDist) { //400
                        if(player.bag.size() < Player.getMaxBagSize()){
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

                //Event was handled by our code, do not launch default behaviour.
                return true;
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("test", "Connecté");

        //Verify user's access permission to localisation
        if (PermissionHandler.checkLocationPermission(this)) {
            Log.d("test", "Connecté - Permissions accordées");

            //If permissions are accepted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //activate user's localisation update
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, REQUEST, this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("test", "OnRequestPersmissionResult");

        //Request code
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                Log.d("test", "OnRequestPersmissionResult - Request code localisation");

                //Authorized permission
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("test", "OnRequestPersmissionResult - Accès autorisé");
                }
                //Unauthorized permission
                else {
                    Log.d("test", "OnRequestPersmissionResult - Accès refusé");
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //Recuperation of player's position
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //Update localisation listener
        if (mMapLocationListener != null) {
            mMapLocationListener.onLocationChanged(location);
        }

        //Player's maker options
        MarkerOptions markerOptions = new MarkerOptions();

        //If the marker does not exist yet
        if(myPositionMarker==null) {

            //Declaration and creation of player's marker options
            myPositionMarker = mMap.addMarker(
                    new MarkerOptions().flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pepito))
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(location.getLatitude(), location.getLongitude())));
        }

        //Update player's marker position
        else{
            Animation.animateMarker(latLng, myPositionMarker, false, mMap);
        }

        //Update google map camera animation
        CameraPosition cam = new CameraPosition.Builder().target(latLng).bearing(currentBearing).zoom(18f).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam));

        Log.d("gentimer", Long.toString(NachosGenerator.generationTimer - System.currentTimeMillis()));

        //TODO : Maybe change this part and put it in a timer callback?
        //Create new Nachos and Item
        if (System.currentTimeMillis() > NachosGenerator.generationTimer) {
            Nachos newNachos = NachosGenerator.addNewWildNachos(myPositionMarker, player.getMeanLevelTeam());
            Marker mNachos = placeMarker(newNachos);
            mapMarkerNachos.put(mNachos, newNachos);

            Item newItem = ItemsGenerator.addNewItem(myPositionMarker);
            Marker mItem = placeMarker(newItem);
            mapMarkerItems.put(mItem, newItem);

            //verify existing markable objects timeouts
            deleteTimedOutObjects(mapMarkerNachos, 600000);
            deleteTimedOutObjects(mapMarkerItems, 12000000);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Compass calibrations (two ways)
        if(depreciatedOrientation){
            if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

                //Update player's marker and camera
                updateRotationPlayerMarker(event.values[0], 1);
                updateRotationCamera(event.values[0], 10);
            }
        }
        else{

            //Recuperation of data depending of sensor
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

            //If the accelerometer et magnetometer give both infos...
            if (hasGravityData && hasGeomagneticData) {
                float identityMatrix[] = new float[9];
                float rotationMatrix[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, identityMatrix, gravityData, geomagneticData);

                if (success) {
                    float orientationMatrix[] = new float[3];
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotationMatrix);

                    //Recuperation of the azimuth which makes the compass work
                    SensorManager.getOrientation(rotationMatrix, orientationMatrix);
                    float rotationInRadians = orientationMatrix[0];
                    rotationInDegrees = (float)(Math.toDegrees(rotationInRadians)+360)%360;

                    Log.d("test", Double.toString(rotationInDegrees));

                    //update player's marker and camera
                    updateRotationPlayerMarker((float)rotationInDegrees, 30);
                    updateRotationCamera((float)rotationInDegrees, 10);
                }
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs.
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("test", "Connexion suspendue");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("test", "Connexion échouée");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { Log.d("test", "onAccuracyChanged"); }

    /**
     * Updates Google Map's angle camera
     *
     * @param bearing angle
     * @param precision minimum angle difference before trigger
     */
    private void updateRotationCamera(float bearing, int precision) {

        //If map already initialized or a position already exists...
        if(mMap != null && latLng != null)
        {
            //If angle is different from actual position
            if(Math.abs(currentBearing - bearing) > precision){

                //Update camera with animation
                CameraPosition currentPlace = new CameraPosition.Builder().target(latLng).bearing(bearing).zoom(18f).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
            }
        }

        //Save new angle
        currentBearing = bearing;
    }

    /**
     * Updates user's marker
     *
     * @param bearing angle
     * @param precision minimum angle difference before trigger
     */
    private void updateRotationPlayerMarker(float bearing, int precision) {

        //If user's marker already initialized and angle difference is big enough...
        if(myPositionMarker != null && Math.abs(myPositionMarker.getRotation()-bearing) > precision){

            //If time has passed enough since last rotation...
            if(SystemClock.uptimeMillis() - lastPlayerRotation > 1000){

                //Update marker with animation
                Animation.rotateMarker(myPositionMarker, bearing);
                lastPlayerRotation = SystemClock.uptimeMillis();
            }
        }
    }

    /**
     * Verify existing object timeouts and delete them if needed
     *
     * @param mapMarker list of objects
     * @param timeout time before deletion in ms
     * @param <T> object implementing markable
     */
    public <T extends Markable> void deleteTimedOutObjects(HashMap<Marker, T> mapMarker, long timeout) {
        Log.d("Generic", "deleteTimedOutObjects");
        for (Iterator<Map.Entry<Marker, T>> iterator = mapMarker.entrySet().iterator(); iterator.hasNext(); )
        {
            Map.Entry<Marker, T> entry = iterator.next();
            Marker currentMarker = entry.getKey();
            T currentObj = (T) entry.getValue();
            if (currentObj.createdTime < (System.currentTimeMillis() - timeout)) {
                iterator.remove();
                currentMarker.remove();
                mapMarker.remove(currentMarker);
            }
        }
    }

    /**
     * Puts a new given object marker on the map
     *
     * @param object object to put on map
     * @param <T> object implementing markable
     * @return given object marker
     */
    private <T extends Markable > Marker placeMarker(T object) {
        Log.d("Generic", "placeMarker");
        Marker marker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(object.getName().toLowerCase(), "drawable", getPackageName())))
                .position(object.getPosition()));
        return marker;
    }

    /**
     * Updates the display showing team infos at the bottom of the screen
     *
     * @param buttonList list of imagebuttons
     * @param progressBarList list of progressbar
     */
    public void updateDisplayInfoTeam(ArrayList<ImageButton> buttonList, ArrayList<ProgressBar> progressBarList){
        for (int i = 0; i < Player.getMaxTeamSize(); i++) {
            if (i < player.team.size()) {
                buttonList.get(i).setImageResource(getResources().getIdentifier(player.team.get(i).getName().toLowerCase(), "drawable", getPackageName()));
                progressBarList.get(i).setProgress(player.team.get(i).getHpPercent());
                buttonList.get(i).setVisibility(View.VISIBLE);
                progressBarList.get(i).setVisibility(View.VISIBLE);
            }

            //Hide empty team emplacements
            else {
                buttonList.get(i).setVisibility(View.INVISIBLE);
                progressBarList.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Adds first nachos to the team
     *
     * @param stringNacho name of nachos
     */
    public void chooseStarterNachos(String stringNacho) {
        player.team.add(0, NachosGenerator.AddNewSpecificNachos(stringNacho));
        updateDisplayInfoTeam(buttonListBottomTeamNachos, progressBarListBottomTeamNachos);
    }

    /**
     * Shows a dialog when no nachos in team
     */
    public void showStarterDialog(){
        if(player.team.isEmpty()){
            StarterDialog starterDialog = new StarterDialog(this);
            starterDialog.setCancelable(false);
            starterDialog.show();
        }
    }
}