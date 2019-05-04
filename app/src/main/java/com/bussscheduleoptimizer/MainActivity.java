package com.bussscheduleoptimizer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bussscheduleoptimizer.utils.LocationUtils;
import com.bussscheduleoptimizer.utils.TFLiteUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private static final float DEFAULT_ZOOM = 16f;
    private static final String TAG = MainActivity.class.getName();

    private boolean mLocationPermissionsGranted = false;
    private static final LatLng mDefaultLocation = new LatLng(46.772939, 23.621713);

    Interpreter tflite;
    GoogleMap map;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location mLastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // check location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mLocationPermissionsGranted = true;
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        try {
            tflite = new Interpreter(TFLiteUtils.loadModelFile(getApplicationContext(), this.getAssets()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("route").document("4");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Route route = document.toObject(Route.class);
                        Toast.makeText(getApplicationContext(), "Route1: " + route.getRoute1() + " Route2: " + route.getRoute2(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                float prediction = doInference(Float.valueOf(idFrom.getText().toString()),
//                        Float.valueOf(idTo.getText().toString()),
//                        Float.valueOf(vehicleType.getText().toString()),
//                        Float.valueOf(month.getText().toString()),
//                        Float.valueOf(day.getText().toString()),
//                        Float.valueOf(hour.getText().toString()),
//                        Float.valueOf(minute.getText().toString()),
//                        Float.valueOf(holiday.getText().toString()),
//                        Float.valueOf(vacation.getText().toString()),
//                        Float.valueOf(temperature.getText().toString()),
//                        Float.valueOf(pType.getText().toString())
//                );
//                String text = prediction + "";
//                secondsDelay.setText(text);
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnPoiClickListener(this);

        LatLng cluj = new LatLng(46.802792, 23.617358);
        map.addMarker(new MarkerOptions().position(cluj).title("Cluj"));
        map.moveCamera(CameraUpdateFactory.newLatLng(cluj));
        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mLastKnownLocation = null;
                return;
            }
            map.setMyLocationEnabled(true);
            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference stationReference = db.collection("station");
        Query stationQuery = stationReference
                .whereEqualTo("name", poi.name);
        Log.i("onPoiClick: ", poi.name);
        stationQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Station station = document.toObject(Station.class);
                        Toast.makeText(getApplicationContext(),"station name: "  + station.getName() + " busses: " + station.getBusses() , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Query failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionsGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && LocationUtils.isLocationServiceEnabled(getApplicationContext()) && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }








}
