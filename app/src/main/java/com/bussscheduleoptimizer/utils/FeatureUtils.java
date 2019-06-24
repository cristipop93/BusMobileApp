package com.bussscheduleoptimizer.utils;

import com.bussscheduleoptimizer.model.PrecipitationType;
import com.google.android.gms.awareness.state.Weather;

import java.util.Calendar;

import static com.bussscheduleoptimizer.MapFragment.weather;

public class FeatureUtils {
    public static float getTemperature() {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_temperature;
        }
        return weather.getTemperature(Weather.CELSIUS);
    }

    public static float getConditions() {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_pType;
        }
        int condition = 1;
        int[] conditions = weather.getConditions();
        if (conditions.length > 0) {
            condition = conditions[0];
        }
        return convertCondition(condition);
    }
    private static float convertCondition(int condition) {
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

    public static float getDayOfWeek(Calendar calendar) {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_day;
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    public static float getMonth(Calendar calendar) {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_month;
        }
        return calendar.get(Calendar.MONTH);
    }

    public static float getHour(Calendar calendar) {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_hour;
        }
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static float getMinute(Calendar calendar) {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_minute;
        }
        return calendar.get(Calendar.MINUTE);
    }

    public static float getVacation(Calendar calendar) {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_vacation;
        }
        return 0;
    }

    public static float getHoliday(Calendar calendar) {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_holiday;
        }
        return 0;
    }

    public static float getVehicleTypeId(int vehicleTypeId) {
        if (TFLiteUtils.useTestData) {
            return TFLiteUtils.s_vehicleType;
        }
        return vehicleTypeId;
    }
}
