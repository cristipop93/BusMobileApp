package com.bussscheduleoptimizer.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bussscheduleoptimizer.RouteCalculator;
import com.bussscheduleoptimizer.model.Arrival;
import com.bussscheduleoptimizer.model.PrecipitationType;
import com.bussscheduleoptimizer.R;
import com.bussscheduleoptimizer.model.Station;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bussscheduleoptimizer.MainActivity.tflite;

public class TFLiteUtils {

    public static float s_vehicleType, s_month, s_day, s_hour, s_minute, s_holiday, s_vacation, s_temperature, s_pType;
    public static boolean useTestData;

    public static MappedByteBuffer loadModelFile(Context applicationContext, AssetManager assets) throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(applicationContext.getResources().getString(R.string.tflite_file));
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public static float doInference(float idFrom, float idTo, float vehicleType, float month, float day, float hour, float minute, float holiday, float vacation, float temperature, float pType) {
        Log.i(TFLiteUtils.class.getName(), "from: " + idFrom + " to: " + idTo + " vehicleType: " + vehicleType + " month: " + month + " day: " + day + " hour: " + hour + " minute: " + minute + " holiday: " + holiday + " vacation: " + vacation + " temp: " + temperature + " pType: " + pType);
        Object[] inputVals = new Object[11];
        inputVals[0] = new float[]{day};
        inputVals[1] = new float[]{holiday};
        inputVals[2] = new float[]{hour};
        inputVals[3] = new float[]{idFrom};
        inputVals[4] = new float[]{idTo};
        inputVals[5] = new float[]{minute};
        inputVals[6] = new float[]{month};
        inputVals[7] = new float[]{pType};
        inputVals[8] = new float[]{temperature};
        inputVals[9] = new float[]{vacation};
        inputVals[10] = new float[]{vehicleType};

        float[][] outputVal = new float[1][1];
        Map<Integer, Object> outputs = new HashMap<>();
        outputs.put(0, outputVal);

        tflite.runForMultipleInputsOutputs(inputVals, outputs);

        return outputVal[0][0];
    }

    public static String interpret(Integer vehicleTypeId, List<Integer> routeToStation, List<Integer> schedule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        float hour = FeatureUtils.getHour(calendar);
        float minute = FeatureUtils.getMinute(calendar);
        float delay = RouteCalculator.getDelay(vehicleTypeId, hour, minute, routeToStation);
        return interpret(RouteCalculator.getStartingTime(schedule, hour, minute, delay), delay);
    }

    public static String interpret(int startingTime, float delay) {

        int delayMinutes = Math.round(delay) / 60; // transform to minutes
        int delaySeconds = Math.round(delay) % 60; // transform to seconds
        Log.i(TFLiteUtils.class.getName(), "delayM: " + delayMinutes + " delayS: " + delaySeconds);
        int startHour = startingTime / 100;
        int startMinutes = startingTime % 100;
        startMinutes += delayMinutes;
        if (startMinutes % 60 != startMinutes) {
            startHour++;
            startMinutes = startMinutes % 60;
        }
        return startHour + ":" + startMinutes + ":" + delaySeconds;
    }

    public static float getDelay(List<Integer> routeToStation, float hour, float minute, float temperature, float condition, float dayOfWeek, float month, float vacation, float holiday, float vehicleType) {
        float delay = 0;
        int station = routeToStation.get(0);
        for (int i = 1; i < routeToStation.size(); i++) {
            delay += doInference(station, routeToStation.get(i), vehicleType, month, dayOfWeek, hour, minute, holiday, vacation, temperature, condition);
            station = routeToStation.get(i);
        }
        return delay;
    }


    public static int getClosestTime(List<Integer> schedule, int currentTime, int delayMinutes) {
        // order ascending
        Collections.sort(schedule, (a, b) -> a > b ? 1 : a < b ? -1 : 0);
        for (Integer integer : schedule) {
            int dHour = integer / 100;
            int dMinutes = integer % 100 + delayMinutes;
            if (dMinutes % 60 != dMinutes) {
                dMinutes = dMinutes % 60;
                dHour++;
            }
            if (dHour * 100 + dMinutes >= currentTime) {
                return integer;
            }
        }
        return 600;
    }


}
