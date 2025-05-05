package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.MinByCreationDateResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Set;

public class MinByCreationDate extends Command {
    CollectionManager collectionManager;

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

        Ticket minTicket = collection.iterator().next();
        for (Ticket t : collection) {
            if (t.compareTo(minTicket) < 0) {
                minTicket = t;
            }
        }

        return new MinByCreationDateResponse(true, minTicket, null);
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "";
    }
}
