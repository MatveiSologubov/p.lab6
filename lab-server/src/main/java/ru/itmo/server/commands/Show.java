package ru.itmo.server.commands;


import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.network.responses.ShowResponse;
import ru.itmo.server.managers.CollectionManager;

import java.util.Set;

/**
 * 'Show' command prints all Tickets in collection
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
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    @Override
    public Response execute(Request request) throws WrongAmountOfArgumentsException {
        if (request.arguments().length != 0) {
            throw new WrongAmountOfArgumentsException(0, request.arguments().length);
        }

        Set<Ticket> tickets = collectionManager.getCollection();

        return new ShowResponse(tickets);
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "This command will show current collection";
    }
}
