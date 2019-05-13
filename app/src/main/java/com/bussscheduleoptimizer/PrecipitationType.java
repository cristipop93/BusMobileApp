package com.bussscheduleoptimizer;

public enum PrecipitationType {
    DRY(0),
    RAIN(1),
    SNOW(2);

    private int id;

    PrecipitationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PrecipitationType getById(int id) {
        for (PrecipitationType value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}