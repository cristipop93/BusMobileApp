package com.bussscheduleoptimizer.utils;

import com.bussscheduleoptimizer.model.PrecipitationType;
import com.google.android.gms.awareness.state.Weather;

import java.util.Calendar;

import static com.bussscheduleoptimizer.MapFragment.weather;

public class FeatureUtils {
    public static float getTemperature() {
        return weather.getTemperature(Weather.CELSIUS);
    }

    public static int getConditions() {
        int condition = 1;
        int[] conditions = weather.getConditions();
        if (conditions.length > 0) {
            condition = conditions[0];
        }
        return convertCondition(condition);
    }
    private static int convertCondition(int condition) {
        switch (condition) {
            case Weather.CONDITION_CLEAR:
                return PrecipitationType.DRY.getId();
            case Weather.CONDITION_CLOUDY:
                return PrecipitationType.RAIN.getId();
            case Weather.CONDITION_FOGGY:
                return PrecipitationType.RAIN.getId();
            case Weather.CONDITION_HAZY:
                return PrecipitationType.DRY.getId();
            case Weather.CONDITION_ICY:
                return PrecipitationType.SNOW.getId();
            case Weather.CONDITION_RAINY:
                return PrecipitationType.RAIN.getId();
            case Weather.CONDITION_SNOWY:
                return PrecipitationType.SNOW.getId();
            case Weather.CONDITION_STORMY:
                return PrecipitationType.RAIN.getId();
            case Weather.CONDITION_UNKNOWN:
                return PrecipitationType.DRY.getId();
            case Weather.CONDITION_WINDY:
                return PrecipitationType.DRY.getId();
            default:
                return 1;
        }
    }

    public static int getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    public static int getHour(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Calendar calendar) {
        return calendar.get(Calendar.MINUTE);
    }

    public static int getVacation(Calendar calendar) {
        return 0;
    }

    public static int getHoliday(Calendar calendar) {
        return 0;
    }
}
