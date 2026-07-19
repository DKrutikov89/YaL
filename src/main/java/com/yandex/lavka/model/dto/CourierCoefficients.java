package com.yandex.lavka.model.dto;

import com.yandex.lavka.model.enums.CourierType;

public final class CourierCoefficients {

    private CourierCoefficients() {
    }

    public static int earningsCoefficient(CourierType courierType) {
        return switch (courierType) {
            case FOOT -> 2;
            case BIKE -> 3;
            case AUTO -> 4;
        };
    }

    public static int ratingCoefficient(CourierType courierType) {
        return switch (courierType) {
            case FOOT -> 3;
            case BIKE -> 2;
            case AUTO -> 1;
        };
    }
}
