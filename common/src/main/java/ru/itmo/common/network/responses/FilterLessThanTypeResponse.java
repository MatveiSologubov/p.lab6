package ru.itmo.common.network.responses;

import ru.itmo.common.models.Ticket;

import java.util.Set;

public class FilterLessThanTypeResponse extends Response {
    private final boolean success;
    private final String message;
    private final Set<Ticket> tickets;

    public FilterLessThanTypeResponse(boolean success, String message, Set<Ticket> tickets) {
        super("filter_less_than_type");
        this.success = success;
        this.message = message;
        this.tickets = tickets;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }
}
