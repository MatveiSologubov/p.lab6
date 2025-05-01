package ru.itmo.common.network.requests;

public class RemoveByIdRequest extends Request {
    private final long id;

    public RemoveByIdRequest(long id) {
        super("remove_by_id");
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
