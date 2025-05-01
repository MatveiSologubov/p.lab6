package ru.itmo.common.network.requests;

import ru.itmo.common.models.Ticket;

public class UpdateRequest extends Request {
    private final long id;
    private final Ticket ticket;

    public UpdateRequest(long id, Ticket ticket) {
        super("update");
        this.id = id;
        this.ticket = ticket;
    }

    public long getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
