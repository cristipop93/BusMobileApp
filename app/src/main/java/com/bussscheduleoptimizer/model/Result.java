package com.bussscheduleoptimizer.model;

import java.util.List;

public class Result {
    String busId;
    VehicleType vehicleType;
    List<Integer> routeToStation;
    String delay;
    List<Integer> completeRoute;
    int stationId;

    public Result(String busId, VehicleType vehicleType, List<Integer> routeToStation, String delay, List<Integer> completeRoute, int stationId) {
        this.busId = busId;
        this.vehicleType = vehicleType;
        this.routeToStation = routeToStation;
        this.delay = delay;
        this.completeRoute = completeRoute;
        this.stationId = stationId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<Integer> getRouteToStation() {
        return routeToStation;
    }

    public void setRouteToStation(List<Integer> routeToStation) {
        this.routeToStation = routeToStation;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public List<Integer> getCompleteRoute() {
        return completeRoute;
    }

    public void setCompleteRoute(List<Integer> completeRoute) {
        this.completeRoute = completeRoute;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }
}
