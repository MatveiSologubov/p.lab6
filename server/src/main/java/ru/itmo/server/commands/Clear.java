package ru.itmo.server.commands;


import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.ClearResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.managers.CollectionManager;

/**
 * 'Clear' command empties collection
 */
public class Clear extends Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     */
    @Override
    public Response execute(Request request) {
        collectionManager.clearCollection();

        return new ClearResponse();
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Clears the collection";
    }
}
