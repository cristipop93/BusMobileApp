package com.bussscheduleoptimizer;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StationDialog {

    public static void showDialog(Station station, Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(station.getName())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        StringBuilder contentBuilder = new StringBuilder();
        for (Integer buss : station.getBusses()) {
            getBusRoute(buss, contentBuilder);
//            contentBuilder.append(buss).append("\t:");
//            contentBuilder.append("\n");
        }
        builder.setMessage(contentBuilder.toString());
        builder.create().show();
    }

    private static void getBusRoute(final Integer buss, final StringBuilder contentBuilder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference routeReference = db.collection("route").document(buss.toString());
        routeReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Route route = task.getResult().toObject(Route.class);
                    if (route != null) {
                        contentBuilder.append(buss).append("\t:").append(route.getRoute1()).append("; ").append(route.getRoute2());
                        contentBuilder.append("\n");
                        Log.i(StationDialog.class.getName(), buss + " " + route.getRoute1() + " " + route.getRoute2());
                    }
                } else {
                    Log.e(StationDialog.class.getName(), "Query failed");
                }
            }
        });

    }
}
