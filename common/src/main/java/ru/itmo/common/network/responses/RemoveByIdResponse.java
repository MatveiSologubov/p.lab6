package ru.itmo.common.network.responses;

public class RemoveByIdResponse extends Response {
    private final boolean success;
    private final String message;

    public RemoveByIdResponse(boolean success, String message) {
        super("remove_by_id");
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
