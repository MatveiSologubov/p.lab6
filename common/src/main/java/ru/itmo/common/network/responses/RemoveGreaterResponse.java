package ru.itmo.common.network.responses;

public class RemoveGreaterResponse extends Response {
    private final String message;

    public RemoveGreaterResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
