package com.bussscheduleoptimizer;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Route {
    private List<Integer> route1;
    private List<Integer> route2;

    public Route(List<Integer> route1, List<Integer> route2) {
        this.route1 = route1;
        this.route2 = route2;
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
}
