package ru.itmo.common.network.responses;

public class AddResponse extends Response {
    private final boolean success;

    public AddResponse(boolean success) {
        super("add");
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
