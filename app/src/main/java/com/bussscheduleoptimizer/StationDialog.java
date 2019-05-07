package com.bussscheduleoptimizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class StationDialog {

    public static void showDialog(Station station, String stationId, Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(station.getName())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        getBusses(station.getBusses(), builder, Integer.parseInt(stationId));
    }

    private static void getBusses(final List<Integer> busses, final AlertDialog.Builder builder, final int stationId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("route").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    StringBuilder contentBuilder = new StringBuilder();
                    if (task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            if (busses.contains(Integer.parseInt(document.getId()))) {
                                Route route = document.toObject(Route.class);
                                if (route != null) {
                                    List<Integer> routeToStation = getRouteToStation(stationId, route);
                                    contentBuilder.append(document.getId())
                                            .append("\t: ")
                                            .append(routeToStation)
                                            .append("\n");
                                }
                            }
                        }
                        builder.setMessage(contentBuilder.toString());
                        builder.create().show();
                    }
                } else {
                    Log.e(StationDialog.class.getName(), "Query failed");
                }
            }
        });
    }

    private static List<Integer> getRouteToStation(int stationId, Route route) {
        int index;
        List<Integer> stations = null;
        if ((index = route.getRoute1().indexOf(stationId)) != -1) {
            stations = route.getRoute1().subList(0, index + 1);
        } else if ((index = route.getRoute2().indexOf(stationId)) != -1) {
            stations = route.getRoute2().subList(0, index + 1);
        }
        return stations;
    }
}
