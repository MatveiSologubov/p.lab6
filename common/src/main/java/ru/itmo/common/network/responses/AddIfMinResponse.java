package ru.itmo.common.network.responses;

public class AddIfMinResponse extends Response {
    private final boolean success;
    private final String message;

    public AddIfMinResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
