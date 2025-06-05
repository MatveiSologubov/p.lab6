package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMinRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddIfMinResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Collections;

/**
 * 'Add If Min' command adds Ticket to collection if its valid and has minimum price
 */
public class AddIfMin extends Command {
    private final CollectionManager collectionManager;

    public AddIfMin(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        AddIfMinRequest addRequest = (AddIfMinRequest) request;
        Ticket ticket = new Ticket(addRequest.getTicket());

        if (collectionManager.getCollection().isEmpty()) {
            collectionManager.add(ticket);
            return new AddIfMinResponse(true, null);
        }

        float minPrice = Collections.min(collectionManager.getCollection()).getPrice();
        if (ticket.getPrice() < minPrice) {
            collectionManager.add(ticket);
            return new AddIfMinResponse(true, null);
        }

        return new AddIfMinResponse(false, "Ticket not added. Current min price is " + minPrice);
    }
}
