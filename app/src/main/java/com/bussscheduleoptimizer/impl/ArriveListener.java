package com.bussscheduleoptimizer.impl;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bussscheduleoptimizer.model.Arrival;
import com.bussscheduleoptimizer.model.VehicleType;
import com.bussscheduleoptimizer.utils.FeatureUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class ArriveListener implements View.OnClickListener {

    public static final String TAG = ArriveListener.class.getName();
    private final Context context;
    private final String busId;
    private final VehicleType vehicleType;
    private final int stationId;

    public ArriveListener(Context context, String busId, VehicleType vehicleType, int stationId) {
        this.context = context;
        this.busId = busId;
        this.vehicleType = vehicleType;
        this.stationId = stationId;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: clicked on:" + busId + " arrived");

        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        float temperature = FeatureUtils.getTemperature();
        int condition = FeatureUtils.getConditions();
        int holiday = FeatureUtils.getHoliday(calendar);
        int vacation = FeatureUtils.getVacation(calendar);

        Arrival arrival = new Arrival(stationId, busId, vehicleType.getIconId(), temperature, condition, vacation, holiday, currentDate, 0);

        RateDialog rateDialog = new RateDialog(context, arrival);
        rateDialog.show();
//        createSchedule();

    }

    private void createSchedule() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 24
        DocumentReference ref = db.collection("route").document("24");
        addSchedule1(3, ref);
        DocumentReference ref2 = db.collection("route").document("24");
        addSchedule2(3, ref2);
        //48
        DocumentReference ref3 = db.collection("route").document("48");
        addSchedule1(6, ref3);
        DocumentReference ref4 = db.collection("route").document("48");
        addSchedule2(6, ref4);
        //4
        DocumentReference ref5 = db.collection("route").document("4");
        addSchedule1(10, ref5);
        DocumentReference ref6 = db.collection("route").document("4");
        addSchedule2(10, ref6);
    }

    private void addSchedule1(int time, DocumentReference ref) {
        for (int hour = 6; hour < 23; hour++) {
            for (int minute = 0; minute < 60; minute +=time) {
                int result = hour * 100 + minute;
                ref.update("schedule1", FieldValue.arrayUnion(result));
            }
        }
    }
    private void addSchedule2(int time, DocumentReference ref) {
        for (int hour = 6; hour < 23; hour++) {
            for (int minute = 0; minute < 60; minute +=time) {
                int result = hour * 100 + minute;
                ref.update("schedule2", FieldValue.arrayUnion(result));
            }
        }
    }
}
