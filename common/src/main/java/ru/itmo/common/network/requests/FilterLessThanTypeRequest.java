package ru.itmo.common.network.requests;

import ru.itmo.common.models.TicketType;

public class FilterLessThanTypeRequest extends Request {
    private final TicketType type;

    public FilterLessThanTypeRequest(TicketType type) {
        super("filter_less_than_type");
        this.type = type;
    }

    public TicketType getType() {
        return type;
    }
}
