package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMinRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddIfMinResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Collections;

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
        Ticket ticket = new Ticket();
        ticket.update(addRequest.getTicket());

        if (collectionManager.getCollection().isEmpty()) {
            collectionManager.add(ticket);
            return new AddIfMinResponse(true, "Added Ticket with price " + ticket.getPrice() + " to collection");
        }

        Ticket minTicket = Collections.min(collectionManager.getCollection());
        if (ticket.compareTo(minTicket) < 0) {
            collectionManager.add(ticket);
            return new AddIfMinResponse(true, "Added Ticket with price " + ticket.getPrice() + " to collection");
        }

        return new AddIfMinResponse(false, "Ticket not added to collection. Current min price is " + minTicket.getPrice());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "";
    }
}
