package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.FilterGreaterThanPriceRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.FilterGreaterThanPriceResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 'Filter Greater Than Price' command returns every Ticket which has price greater than specified
 */
public class FilterGreaterThanPrice extends Command {
    private final CollectionManager collectionManager;

    public FilterGreaterThanPrice(CollectionManager collectionManager) {
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
            return new FilterGreaterThanPriceResponse(false, "Collection is empty", null);
        }

        FilterGreaterThanPriceRequest filterGreaterThanPriceRequest = (FilterGreaterThanPriceRequest) request;
        float price = filterGreaterThanPriceRequest.getPrice();

        Set<Ticket> result = collectionManager.getCollection().stream()
                .filter(t -> t.getPrice() != null && t.getPrice() > price)
                .collect(Collectors.toSet());

        return new FilterGreaterThanPriceResponse(true, null, result);
    }
}
