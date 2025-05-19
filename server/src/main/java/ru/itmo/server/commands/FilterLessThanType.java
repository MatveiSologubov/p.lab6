package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.models.TicketType;
import ru.itmo.common.network.requests.FilterLessThanTypeRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.FilterLessThanTypeResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Set;
import java.util.stream.Collectors;

public class FilterLessThanType extends Command {
    private final CollectionManager collectionManager;

    public FilterLessThanType(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        Set<Ticket> collection = collectionManager.getCollection();
        if (collection == null || collection.isEmpty()) {
            return new FilterLessThanTypeResponse(false, "Collection is empty", null);
        }

        FilterLessThanTypeRequest filterRequest = (FilterLessThanTypeRequest) request;

        TicketType type = filterRequest.getType();
        Set<Ticket> result = collectionManager.getCollection().stream()
                .filter(t -> t.getType() == null || t.getType().compareTo(type) < 0)
                .collect(Collectors.toSet());

        return new FilterLessThanTypeResponse(true, null, result);
    }
}
