package com.bussscheduleoptimizer.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Arrivals {
    int stationId;
    String busId;
    int vehicleType;
    float temperature;
    int condition;
    int vacation, holiday;
    Date completeDate;
    int crowdedLevel;

    public Arrivals(int stationId, String busId, int vehicleType, float temperature, int condition, int vacation, int holiday, Date completeDate, int crowdedLevel) {
        this.stationId = stationId;
        this.busId = busId;
        this.vehicleType = vehicleType;
        this.temperature = temperature;
        this.condition = condition;
        this.vacation = vacation;
        this.holiday = holiday;
        this.completeDate = completeDate;
        this.crowdedLevel = crowdedLevel;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int isVacation() {
        return vacation;
    }

    public void setVacation(int vacation) {
        this.vacation = vacation;
    }

    public int isHoliday() {
        return holiday;
    }

    public void setHoliday(int holiday) {
        this.holiday = holiday;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public int getCrowdedLevel() {
        return crowdedLevel;
    }

    public void setCrowdedLevel(int crowdedLevel) {
        this.crowdedLevel = crowdedLevel;
    }
}
