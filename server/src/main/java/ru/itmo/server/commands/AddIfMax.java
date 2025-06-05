package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMaxRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddIfMaxResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Collections;

/**
 * 'Add If Max' command adds Ticket to collection if its valid and has maximum price
 */
public class AddIfMax extends Command {
    private final CollectionManager collectionManager;

    public AddIfMax(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        AddIfMaxRequest addRequest = (AddIfMaxRequest) request;
        Ticket ticket = new Ticket(addRequest.getTicket());

        if (collectionManager.getCollection().isEmpty()) {
            collectionManager.add(ticket);
            return new AddIfMaxResponse(true, null);
        }

        float maxPrice = Collections.max(collectionManager.getCollection()).getPrice();
        if (ticket.getPrice() > maxPrice) {
            collectionManager.add(ticket);
            return new AddIfMaxResponse(true, null);
        }

        return new AddIfMaxResponse(false, "Ticket not added to collection. Current max price is " + maxPrice);
    }
}
