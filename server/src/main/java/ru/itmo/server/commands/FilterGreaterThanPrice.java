package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.FilterGreaterThanPriceRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.FilterGreaterThanPriceResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.HashSet;
import java.util.Set;

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

        Set<Ticket> result = new HashSet<>();
        for (Ticket ticket : collectionManager.getCollection()) {
            float currentPrice = 0;
            if (ticket.getPrice() != null) {
                currentPrice = ticket.getPrice();
            }

            if (Float.compare(currentPrice, price) > 0) {
                result.add(ticket);
            }
        }

        return new FilterGreaterThanPriceResponse(true, null, result);
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "";
    }
}
