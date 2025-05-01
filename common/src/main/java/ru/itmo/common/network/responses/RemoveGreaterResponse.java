package ru.itmo.common.network.responses;

public class RemoveGreaterResponse extends Response {
    private final boolean success;
    private final String message;

    public RemoveGreaterResponse(boolean success, String message) {
        super("remove_greater");
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
