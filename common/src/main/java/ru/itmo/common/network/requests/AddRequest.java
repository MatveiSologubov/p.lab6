package ru.itmo.common.network.requests;

import ru.itmo.common.models.Ticket;

public class AddRequest extends Request {
    private final Ticket ticket;

    public AddRequest(Ticket ticket) {
        super("add");
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
