package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.requests.UpdateRequest;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.network.responses.UpdateResponse;
import ru.itmo.server.managers.CollectionManager;

/**
 * 'Update' command updates Ticket with specified id with data from new Ticket
 */
public class Update extends Command {
    private final CollectionManager collectionManager;

    public Update(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        UpdateRequest updateRequest = (UpdateRequest) request;
        if (collectionManager.getCollection().isEmpty()) return new UpdateResponse(false, "Collection is empty");

        for (Ticket ticket : collectionManager.getCollection()) {
            if (ticket.getId() == updateRequest.getId()) {
                ticket.update(updateRequest.getTicket());
                return new UpdateResponse(true, null);
            }
        }

        return new UpdateResponse(false, "Ticket with id " + updateRequest.getId() + " not found");
    }
}
