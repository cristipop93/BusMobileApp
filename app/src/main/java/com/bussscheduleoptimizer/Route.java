package com.bussscheduleoptimizer;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Route {
    private List<Integer> route1;
    private List<Integer> route2;
    private List<Integer> schedule1;
    private List<Integer> schedule2;
    private Integer vehicleTypeId;

    public Route(List<Integer> route1, List<Integer> route2, List<Integer> schedule1, List<Integer> schedule2, Integer vehicleTypeId) {
        this.route1 = route1;
        this.route2 = route2;
        this.schedule1 = schedule1;
        this.schedule2 = schedule2;
        this.vehicleTypeId = vehicleTypeId;
    }

    public Route() {
    }

    public List<Integer> getRoute1() {
        return route1;
    }

    public void setRoute1(List<Integer> route1) {
        this.route1 = route1;
    }

    public List<Integer> getRoute2() {
        return route2;
    }

    public void setRoute2(List<Integer> route2) {
        this.route2 = route2;
    }

    public List<Integer> getSchedule1() {
        return schedule1;
    }

    public void setSchedule1(List<Integer> schedule1) {
        this.schedule1 = schedule1;
    }

    public List<Integer> getSchedule2() {
        return schedule2;
    }

    public void setSchedule2(List<Integer> schedule2) {
        this.schedule2 = schedule2;
    }

    public Integer getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Integer vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }
}
