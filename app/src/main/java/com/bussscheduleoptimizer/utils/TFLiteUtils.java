package com.bussscheduleoptimizer.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.bussscheduleoptimizer.model.PrecipitationType;
import com.bussscheduleoptimizer.R;
import com.google.android.gms.awareness.state.Weather;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
        if (useTestData) {
            vehicleType = s_vehicleType;
            month = s_month;
            day = s_day;
            hour = s_hour;
            minute = s_minute;
            holiday = s_holiday;
            vacation = s_vacation;
            temperature = s_temperature;
            pType = s_pType;
        }
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
        float temperature = FeatureUtils.getTemperature();
        int condition = FeatureUtils.getConditions();

        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int dayOfWeek = FeatureUtils.getDayOfWeek(calendar);
        int month = FeatureUtils.getMonth(calendar);
        int hour = FeatureUtils.getHour(calendar);
        int minute = FeatureUtils.getMinute(calendar);
        int vacation = FeatureUtils.getVacation(calendar);
        int holiday = FeatureUtils.getHoliday(calendar);

        int startingTime;
        if (routeToStation.size() == 1) {
            startingTime = getClosestTime(schedule, hour * 100 + minute, 0);
            return (startingTime / 100) + ":" + (startingTime % 100) + ":00";
        } else {
            float delay = 0;
            int station = routeToStation.get(0);
            for (int i = 1; i < routeToStation.size(); i++) {
                delay += doInference(station, routeToStation.get(i), vehicleTypeId, month, dayOfWeek, hour, minute, holiday, vacation, temperature, condition);
                station = routeToStation.get(i);
            }
            int delayMinutes = Math.round(delay) / 60; // transform to minutes
            int delaySeconds = Math.round(delay) % 60; // transform to seconds
            startingTime = getClosestTime(schedule, hour * 100 + minute, delayMinutes);
            Log.i(TFLiteUtils.class.getName(), "delaySec: " + delay + " delayM: " + delayMinutes + " delayS: " + delaySeconds);
            int startHour = startingTime / 100;
            int startMinutes = startingTime % 100;
            startMinutes += delayMinutes;
            if (startMinutes % 60 != startMinutes) {
                startHour++;
                startMinutes = startMinutes % 60;
            }
            return startHour + ":" + startMinutes + ":" + delaySeconds;
        }
    }

    private static int getClosestTime(List<Integer> schedule, int currentTime, int delayMinutes) {
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
