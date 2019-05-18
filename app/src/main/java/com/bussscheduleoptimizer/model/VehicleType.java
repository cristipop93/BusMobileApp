package com.bussscheduleoptimizer.model;

import com.bussscheduleoptimizer.R;

public enum VehicleType {
    BUS(0, R.drawable.bus),
    TROLLEYBUS(1, R.drawable.trolley),
    TRAM(2, R.drawable.tram);

    private final int iconId;
    private int id;

    VehicleType(int id, int iconId) {
        this.id = id;
        this.iconId = iconId;
    }

    public int getId() {
        return id;
    }

    public int getIconId() {
        return iconId;
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