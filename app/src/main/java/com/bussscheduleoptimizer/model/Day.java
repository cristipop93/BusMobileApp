package com.bussscheduleoptimizer.model;

public enum Day {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    int id;

    Day(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Day getById(int id) {
        for (Day value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return null;
    }
}
