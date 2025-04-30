package ru.itmo.common.network.responses;

import java.io.Serializable;

public class Response implements Serializable {
    private final String name;

    public Response(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
