package ru.itmo.common.network.responses;

import ru.itmo.common.models.Ticket;

public class MinByCreationDateResponse extends Response {
    private final boolean success;
    private final Ticket ticket;
    private final String errorMessage;
    public MinByCreationDateResponse(boolean success, Ticket ticket, String errorMessage) {
        super("min_by_creation_date");
        this.success = success;
        this.ticket = ticket;
        this.errorMessage = errorMessage;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
