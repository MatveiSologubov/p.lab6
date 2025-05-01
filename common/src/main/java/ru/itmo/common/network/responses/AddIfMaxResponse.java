package ru.itmo.common.network.responses;

public class AddIfMaxResponse extends Response {
    private final boolean success;
    private final String message;

    public AddIfMaxResponse(boolean success, String message) {
        super("add");
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
