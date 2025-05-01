package ru.itmo.common.network.requests;

import ru.itmo.common.models.Ticket;

public class AddIfMinRequest extends Request {
    private final Ticket ticket;

    public AddIfMinRequest(Ticket ticket) {
        super("add_if_min");
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
