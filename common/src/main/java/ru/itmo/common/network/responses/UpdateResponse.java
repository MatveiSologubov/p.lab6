package ru.itmo.common.network.responses;

public class UpdateResponse extends Response {
    private final boolean success;
    private final String message;

    public UpdateResponse(boolean success, String message) {
        super("update");
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
