package com.bussscheduleoptimizer.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Station {
    private String name;
    private List<Integer> busses;
    private GeoPoint location;

    public Station(String name, List<Integer> busses, GeoPoint location) {
        this.name = name;
        this.busses = busses;
        this.location = location;
    }

    public Station() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getBusses() {
        return busses;
    }

    public void setBusses(List<Integer> busses) {
        this.busses = busses;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
