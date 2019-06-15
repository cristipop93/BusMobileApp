package com.bussscheduleoptimizer.model;

public enum CrowdedLevel {
    EMPTY(0),
    SPARSE(1),
    MODERATE(2),
    CROWDED(3),
    FULL(4);

    int id;

    CrowdedLevel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CrowdedLevel getById(int id) {
        for (CrowdedLevel value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
