package com.bussscheduleoptimizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bussscheduleoptimizer.adapters.RecyclerViewAdapter;
import com.bussscheduleoptimizer.model.Result;
import com.bussscheduleoptimizer.model.Route;
import com.bussscheduleoptimizer.model.Station;
import com.bussscheduleoptimizer.model.VehicleType;
import com.bussscheduleoptimizer.utils.TFLiteUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationDialog {
    private static final String TAG = "StationDialog";

    public static void showDialog(Station station, String stationId, View view, DirectionsCalculator directionsCalculator) {

        getBusses(station.getBusses(), view, Integer.parseInt(stationId), directionsCalculator);
    }

    private static void getBusses(final List<Integer> busses, final View view, final int stationId, DirectionsCalculator directionsCalculator) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("route").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    StringBuilder contentBuilder = new StringBuilder();
                    if (task.getResult() != null) {
                        ArrayList<Result> results = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            if (busses.contains(Integer.parseInt(document.getId()))) {
                                Route route = document.toObject(Route.class);
                                if (route != null) {
                                    List<Integer> routeToStation = getRouteToStation(stationId, route);
                                    List<Integer> completeRoute = getCompleteRoute(stationId, route);
                                    String inferenceResult = TFLiteUtils.interpret(route.getVehicleTypeId(), routeToStation, getSchedule(stationId, route));
                                    contentBuilder.append(document.getId())
                                            .append("-")
                                            .append(VehicleType.getById(route.getVehicleTypeId()))
                                            .append("\t: ")
                                            .append(routeToStation)
                                            .append(": ")
                                            .append(inferenceResult)
                                            .append("\n");
                                    results.add(new Result(document.getId(), VehicleType.getById(route.getVehicleTypeId()), routeToStation, inferenceResult, completeRoute, stationId));

                                }
                            }
                        }
                        initRecyclerView(view, results, directionsCalculator);
                    }
                } else {
                    Log.e(StationDialog.class.getName(), "Query failed");
                }
            }
        });
    }

    private static void initRecyclerView(View view, ArrayList<Result> results, DirectionsCalculator directionsCalculator) {
        Log.d(TAG, "initRecyclerView: init recyclerView");
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(view.getContext(), results, directionsCalculator);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }

    private static List<Integer> getRouteToStation(int stationId, Route route) {
        int index;
        List<Integer> stations = new ArrayList<>();
        if ((index = route.getRoute1().indexOf(stationId)) != -1) {
            stations = route.getRoute1().subList(0, index + 1);
        } else if ((index = route.getRoute2().indexOf(stationId)) != -1) {
            stations = route.getRoute2().subList(0, index + 1);
        }
        return stations;
    }

    private static List<Integer> getSchedule(int stationId, Route route) {
        if (route.getRoute1().contains(stationId)) {
            return route.getSchedule1();
        } else if (route.getRoute2().contains(stationId)) {
            return route.getSchedule2();
        }
        return new ArrayList<>(Collections.singletonList(600)); // default 6
    }

    private static List<Integer> getCompleteRoute(int stationId, Route route) {
        if (route.getRoute1().contains(stationId)) {
            return route.getRoute1();
        } else {
            return route.getRoute2();
        }
    }
}
