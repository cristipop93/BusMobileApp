package com.bussscheduleoptimizer.model;

public enum VehicleType {
    BUS(0),
    TROLLEYBUS(1),
    TRAM(2);

    private int id;

    VehicleType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static VehicleType getById(int id) {
        for (VehicleType value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}