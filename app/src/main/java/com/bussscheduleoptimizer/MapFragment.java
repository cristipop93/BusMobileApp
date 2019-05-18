package com.bussscheduleoptimizer;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bussscheduleoptimizer.model.Station;
import com.bussscheduleoptimizer.utils.LocationUtils;
import com.bussscheduleoptimizer.utils.ViewWeightAnimationWrapper;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private static final float DEFAULT_ZOOM = 16f;
    private static final String TAG = MainActivity.class.getName();
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 1;

    private boolean mLocationPermissionsGranted = false;
    private static final LatLng mDefaultLocation = new LatLng(46.772939, 23.621713);

    public static Weather weather;
    public static Map<String, Station> stations;

    RelativeLayout relativeLayout;
    LinearLayout mapContainer;
    GoogleMap map;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location mLastKnownLocation;
    View myView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.map_page, container, false);
        // check location permission
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mLocationPermissionsGranted = true;
        }
        getWeatherInfo();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        MapView mMapView = (MapView) myView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        relativeLayout = myView.findViewById(R.id.relativeLayout);
        mapContainer = myView.findViewById(R.id.map_container);
        myView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandMapAnimation();
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                map = mMap;

                map.setOnMarkerClickListener(MapFragment.this);
                loadStationMarkers();
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                if (mLocationPermissionsGranted) {
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mLastKnownLocation = null;
                        return;
                    }
                    map.setMyLocationEnabled(true);
                    // Get the current location of the device and set the position of the map.
                    getDeviceLocation();
                }
            }
        });

        return myView;
    }

    private void setStationMarkers() {
        for (Station station : stations.values()) {
            map.addMarker(new MarkerOptions().position(new LatLng(station.getLocation().getLatitude(), station.getLocation().getLongitude())).title(station.getName()));
        }
    }

    private void loadStationMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("station").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    stations = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Station station = document.toObject(Station.class);
                        stations.put(document.getId(), station);
                    }
                    setStationMarkers();
                }
            }
        });
    }


    private void getWeatherInfo() {
        if (!(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Awareness.getSnapshotClient(getActivity().getApplicationContext()).getWeather().addOnCompleteListener(new OnCompleteListener<WeatherResponse>() {
                @Override
                public void onComplete(@NonNull Task<WeatherResponse> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            WeatherResponse weatherResponse = task.getResult();
                            weather = weatherResponse.getWeather();
                            if (weather != null) {
                                Log.i("Weather", weather.getTemperature(Weather.CELSIUS) + " humidity: " + weather.getHumidity() + " conditions: " + Arrays.toString(weather.getConditions()));
                            } else {
                                Log.i("Weather", "null");
                            }
                        }
                    } else {
                        Log.e("Awereness", task.getException().toString());
                    }
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Station selectedStation = null;
        String stationId = null;
        for (String id : stations.keySet()) {
            Station station = stations.get(id);
            if (station == null) {
                continue;
            }
            if (station.getName().equals(marker.getTitle())) {
                selectedStation = station;
                stationId = id;
                break;
            }
        }
        if (selectedStation == null || stationId == null) {
            return false;
        }
        StationDialog.showDialog(selectedStation, stationId, myView);
        if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED)
            contractMapAnimation();
        return false;
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionsGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && LocationUtils.isLocationServiceEnabled(getActivity().getApplicationContext()) && task.getResult() != null) {
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

    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                65,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(relativeLayout);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                35,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
        mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                65);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(relativeLayout);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                35);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
        mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
    }
}
