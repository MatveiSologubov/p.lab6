package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

public class Add extends Command {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        AddRequest addRequest = (AddRequest) request;
        Ticket ticket = new Ticket(addRequest.getTicket());

        collectionManager.add(ticket);
        return new AddResponse(true);
    }
}
