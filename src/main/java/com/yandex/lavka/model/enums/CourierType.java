package com.yandex.lavka.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CourierType {
    FOOT("FOOT"),
    BIKE("BIKE"),
    AUTO("AUTO");

    private final String value;

    CourierType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CourierType fromString(String value) {
        for (CourierType type : CourierType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown courier type: " + value);
    }
}
