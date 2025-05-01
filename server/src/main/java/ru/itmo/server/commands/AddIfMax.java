package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMaxRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddIfMaxResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Collections;

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
        Ticket ticket = new Ticket();
        ticket.update(addRequest.getTicket());

        if (collectionManager.getCollection().isEmpty()) {
            collectionManager.add(ticket);
            return new AddIfMaxResponse(true, "Added Ticket with price " + ticket.getPrice() + " to collection");
        }

        Ticket maxTicket = Collections.max(collectionManager.getCollection());
        if (ticket.compareTo(maxTicket) > 0) {
            collectionManager.add(ticket);
            return new AddIfMaxResponse(true, "Added Ticket with price " + ticket.getPrice() + " to collection");
        }

        return new AddIfMaxResponse(false, "Ticket not added to collection. Current max price is " + maxTicket.getPrice());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "";
    }
}
