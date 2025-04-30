package ru.itmo.server.commands;

import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.InfoResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

import java.time.LocalDateTime;

/**
 * 'Info' command print information about current collection
 */
public class Info extends Command {
    CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        String collectionType = collectionManager.getCollectionType();
        int collectionSize = collectionManager.getCollectionSize();
        LocalDateTime initTime = collectionManager.getInitTime();

        return new InfoResponse(collectionType, collectionSize, initTime);
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "This command print information about current collection.";
    }
}
