package ru.itmo.common.network.requests;

import ru.itmo.common.models.Ticket;

public class RemoveGreaterRequest extends Request {
    private final Ticket ticket;

    public RemoveGreaterRequest(Ticket ticket) {
        super("remove_greater");
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
