package com.bussscheduleoptimizer.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Station {
    private String name;
    private List<Integer> busses;

    public Station(String name, List<Integer> busses) {
        this.name = name;
        this.busses = busses;
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
}
