package ru.itmo.common.network.requests;

import ru.itmo.common.models.Ticket;

public class AddIfMaxRequest extends Request {
    private final Ticket ticket;

    public AddIfMaxRequest(Ticket ticket) {
        super("add_if_max");
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
