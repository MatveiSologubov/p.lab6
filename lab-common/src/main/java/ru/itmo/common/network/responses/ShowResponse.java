package ru.itmo.common.network.responses;

import ru.itmo.common.models.Ticket;

import java.util.Set;

public class ShowResponse extends Response {
    final Set<Ticket> tickets;

    public ShowResponse(Set<Ticket> tickets) {
        super("show");
        this.tickets = tickets;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }
}
