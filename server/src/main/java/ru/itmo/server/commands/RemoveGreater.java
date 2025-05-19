package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.RemoveGreaterRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.RemoveGreaterResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.util.Set;

/**
 * 'Remove Greater' command builds ticket and then removes all tickets with bigger price than built ticket
 */
public class RemoveGreater extends Command {
    private final CollectionManager collectionManager;

    public RemoveGreater(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            return new RemoveGreaterResponse(false, "Collection is empty");
        }

        RemoveGreaterRequest removeGreaterRequest = (RemoveGreaterRequest) request;

        Set<Ticket> collection = collectionManager.getCollection();
        Ticket target = removeGreaterRequest.getTicket();
        collection.removeIf(ticket -> ticket.compareTo(target) > 0);

        return new RemoveGreaterResponse(true, "Collection now contains " + collection.size() + " tickets");
    }
}
