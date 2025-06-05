package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.MinByCreationDateResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * 'Min By Creation Date' command returns Ticket with minimum creation date
 */
public class MinByCreationDate extends Command {
    private final CollectionManager collectionManager;

    public MinByCreationDate(CollectionManager collectionManager) {
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
            return new MinByCreationDateResponse(false, null, "Collection is empty");
        }

        Ticket minTicket = Collections.min(collection, Comparator.comparing(Ticket::getCreationDate));

        return new MinByCreationDateResponse(true, minTicket, null);
    }
}
