package com.example.marcschnaebe.mynacho;


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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import static android.R.attr.start;
import static com.example.marcschnaebe.mynacho.R.id.map;


public class MapsActivity extends FragmentActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationSource,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        SensorEventListener{

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private OnLocationChangedListener mMapLocationListener = null;
    private Marker myPositionMarker;

    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAcceleroSensor;
    private Sensor mOrientationSensor;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private float currentBearing = 0;

    private long lastPlayerRotation = 0;

    private LatLng latLng;

    private Button buttonTest;
    private Button buttonAttack;
    private TextView textInfo;
    private LinearLayout layoutInfo;

    private NachosGenerator nachosGenerator;
    private HashMap<Marker, Nachos> mapMarker = new HashMap<Marker, Nachos>();

    // globals
    private float[] gravityData = new float[3];
    private float[] geomagneticData  = new float[3];
    private boolean hasGravityData = false;
    private boolean hasGeomagneticData = false;
    private double rotationInDegrees;

    private boolean depreciatedOrientation = false;


    // location accuracy settings
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(2000)
            .setFastestInterval(1000)
            .setSmallestDisplacement(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("test", "OnCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        if(depreciatedOrientation){
            mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        else{
            mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mAcceleroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        buttonTest = (Button) findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Toast.makeText(context, "Test", Toast.LENGTH_LONG).show();
            }
        });

        layoutInfo = (LinearLayout) findViewById(R.id.layoutInfo);
        layoutInfo.setVisibility(LinearLayout.GONE);

        buttonAttack = (Button) findViewById(R.id.buttonAttack);
        buttonAttack.setEnabled(false);

        textInfo = (TextView) findViewById(R.id.textInfo);

        nachosGenerator = new NachosGenerator(mMap);


        // List of Sensors Available
        List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // Print how may Sensors are there
        Log.d("sensor", Integer.toString(msensorList.size()));

        // Print each Sensor available using sSensList as the String to be printed
        String sSensList = new String("");
        Sensor tmp;
        int x,i;
        for (i=0;i<msensorList.size();i++){
            tmp = msensorList.get(i);
            sSensList = " "+sSensList+tmp.getName() + "\n"; // Add the sensor name to the string of sensors available
        }
        // if there are sensors available show the list
        if (i>0){
            Log.d("sensor", sSensList);
        }

    }

    @Override
    protected void onResume() {
        Log.d("test", "OnResume");
        super.onResume();
        mGoogleApiClient.connect();

        if(depreciatedOrientation){
            mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAcceleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        mSensorManager.unregisterListener(this);
   
    }

    @Override
    public void onMapReady(GoogleMap map) {

        Log.d("test", "OnMapReady");

        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setLocationSource(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Nachos nachos = mapMarker.get(marker);
                if (nachos != null && myPositionMarker != null) {
                    LatLng myPosition = myPositionMarker.getPosition();
                    LatLng nachosPosition = nachos.getPosition();

                    float[] results = new float[3];
                    double startLatitude = myPosition.latitude;
                    double startLongitude = myPosition.longitude;
                    double endLatitude = nachosPosition.latitude;
                    double endLongitude = nachosPosition.longitude;
                    Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
                    if (results[0] < 40) {
                        layoutInfo.setVisibility(LinearLayout.VISIBLE);
                        textInfo.setText("Nom : " + nachos.getName() + " PV : " + nachos.getPv());
                        buttonAttack.setEnabled(true);
                    }
                    else if (results[0] < 100) {
                        layoutInfo.setVisibility(LinearLayout.VISIBLE);
                        textInfo.setText("Nom : " + nachos.getName() + " PV : " + nachos.getPv());
                        buttonAttack.setEnabled(false);
                    }
                    else {
                        layoutInfo.setVisibility(LinearLayout.VISIBLE);
                        textInfo.setText("Nom : ??? PV : ???");
                        buttonAttack.setEnabled(false);
                    }
                }
                return false;
            }
        });
    }

    public boolean checkLocationPermission() {

        Log.d("test", "CheckLocationPermission");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("test", "CheckLocationPermission - Passage compliqué");


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

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
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {

                Log.d("test", "CheckLocationPermission - Demande de la permission");

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            Log.d("test", "CheckLocationPermission - Passe direct");

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d("test", "OnRequestPersmissionResult");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                Log.d("test", "OnRequestPersmissionResult - Bon request code");

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("test", "OnRequestPersmissionResult - Accès autorisé");

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        Log.d("test", "OnRequestPersmissionResult - Exécution");

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);


                    }

                } else {
                    Log.d("test", "OnRequestPersmissionResult - Refusé");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("test", "Connecté");

        if (checkLocationPermission()) {
            Log.d("test", "Connecté - dans le check");
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //mMap.setMyLocationEnabled(false); // Pour le point bleu sur notre position
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient,
                        REQUEST,
                        this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("test", "Connexion suspendue");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("test", "Connexion echoue");
    }




    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "LocationChanged",Toast.LENGTH_LONG).show();
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();

        if (mMapLocationListener != null) {
            mMapLocationListener.onLocationChanged(location);
        }

//        Toast.makeText(this, "bearing"+bearing,Toast.LENGTH_LONG).show();
        CameraPosition cam = new CameraPosition.Builder().target(latLng).bearing(currentBearing).zoom(18f).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam));

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

        if(myPositionMarker==null) {
            myPositionMarker = mMap.addMarker(
                    new MarkerOptions().flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pepito))
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(location.getLatitude(), location.getLongitude())));
        }
        else{
            Animation.animateMarker(latLng, myPositionMarker, false, mMap);
            //myPositionMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        //Populate da world with Nachomons
        Nachos newNachos = nachosGenerator.addNewWildNachos(myPositionMarker);
        Marker mNachos = placeMarker(newNachos);
        mapMarker.put(mNachos, newNachos);
    }



    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
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
    public void onSensorChanged(SensorEvent event) {

//        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//
//
//            float angle = ((event.values[0] +32) /64)*360;
//
//            Log.d("test", " Value :" + angle + " / Declination : " + mDeclination + " / \n");
//            updateCamera(angle);
//        }

        if(depreciatedOrientation){
            if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {


                //float angle = ((event.values[0] +32) /64)*360;

                //Log.d("test", " Value :" + angle + " / Declination : " + mDeclination + " / \n");
                updateRotationPlayerMarker(event.values[0], 1);
                updateCamera(event.values[0], 10);
            }
        }
        else{
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

            if (hasGravityData && hasGeomagneticData) {
                float identityMatrix[] = new float[9];
                float rotationMatrix[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, identityMatrix, gravityData, geomagneticData);

                if (success) {
                    float orientationMatrix[] = new float[3];
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotationMatrix);

                    SensorManager.getOrientation(rotationMatrix, orientationMatrix);
                    float rotationInRadians = orientationMatrix[0];
                    rotationInDegrees = (float)(Math.toDegrees(rotationInRadians)+360)%360;

                    Log.d("test", Double.toString(rotationInDegrees));
//                // do something with the rotation in degrees
//                CameraPosition oldPos = mMap.getCameraPosition();
//                CameraPosition pos = CameraPosition.builder(oldPos).bearing((float)rotationInDegrees).build();
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

                    updateRotationPlayerMarker((float)rotationInDegrees, 30);
                    updateCamera((float)rotationInDegrees, 10);

                }
            }
        }

    }

    private void updateCamera(float bearing, int precision) {
        if(mMap != null && latLng != null){
//            CameraPosition oldPos = mMap.getCameraPosition();
//            CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
            if(Math.abs(currentBearing - bearing) > precision){
                CameraPosition currentPlace = new CameraPosition.Builder().target(latLng).bearing(bearing).zoom(18f).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
            }


        }

        currentBearing = bearing;

    }

    private void updateRotationPlayerMarker(float angle, int precision) {
        if(myPositionMarker != null && Math.abs(myPositionMarker.getRotation()-angle) > precision){

            if(SystemClock.uptimeMillis() - lastPlayerRotation > 1000){
                Animation.rotateMarker(myPositionMarker, angle);
                //myPositionMarker.setRotation(angle);
                lastPlayerRotation = SystemClock.uptimeMillis();
            }

        }

    }

    public Marker placeMarker(Nachos nachos) {
        Marker mNachos = mMap.addMarker(new MarkerOptions().position(nachos.getPosition()));
        return mNachos;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}