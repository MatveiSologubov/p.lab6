package ru.itmo.common.models;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum TicketType {
    VIP,
    USUAL,
    BUDGETARY,
    CHEAP;

    public static String order() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(" < "));
    }
}
