package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.RemoveByIdRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.RemoveByIdResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

/**
 * 'Remove By ID' command removes Ticket with specified id
 */
public class RemoveById extends Command {
    final CollectionManager collectionManager;

    public RemoveById(CollectionManager collectionManager) {
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
            return new RemoveByIdResponse(false, "Collection is empty");
        }
        RemoveByIdRequest removeByIdRequest = (RemoveByIdRequest) request;
        for (Ticket ticket : collectionManager.getCollection()) {
            if (ticket.getId() == removeByIdRequest.getId()) {
                collectionManager.getCollection().remove(ticket);
                return new RemoveByIdResponse(true, null);
            }
        }
        return new RemoveByIdResponse(false, "Ticket with id " + removeByIdRequest.getId() + " was not found");
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Removes element from collection with specified id";
    }
}
