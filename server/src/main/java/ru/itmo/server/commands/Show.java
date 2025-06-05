package ru.itmo.server.commands;


import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.network.responses.ShowResponse;
import ru.itmo.server.managers.CollectionManager;

import java.util.Set;

/**
 * 'Show' command returns all Tickets in collection
 */
public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager manger) {
        this.collectionManager = manger;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        Set<Ticket> tickets = collectionManager.getCollection();

        return new ShowResponse(tickets);
    }
}
