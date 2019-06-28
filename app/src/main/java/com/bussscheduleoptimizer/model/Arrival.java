package com.bussscheduleoptimizer.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Arrival {
    int stationId;
    String busId;
    int vehicleType;
    float temperature;
    float condition;
    float vacation;
    float holiday;
    Date completeDate;
    int crowdedLevel;

    public Arrival(int stationId, String busId, int vehicleType, float temperature, float condition, float vacation, float holiday, Date completeDate, int crowdedLevel) {
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

    public Arrival() {

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

    public float getCondition() {
        return condition;
    }

    public void setCondition(float condition) {
        this.condition = condition;
    }

    public float isVacation() {
        return vacation;
    }

    public void setVacation(float vacation) {
        this.vacation = vacation;
    }

    public float isHoliday() {
        return holiday;
    }

    public void setHoliday(float holiday) {
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

    @Override
    public String toString() {
        return "Arrival{" +
                "stationId=" + stationId +
                ", busId='" + busId + '\'' +
                ", vehicleType=" + vehicleType +
                ", temperature=" + temperature +
                ", condition=" + condition +
                ", vacation=" + vacation +
                ", holiday=" + holiday +
                ", completeDate=" + completeDate +
                ", crowdedLevel=" + crowdedLevel +
                '}';
    }
}
