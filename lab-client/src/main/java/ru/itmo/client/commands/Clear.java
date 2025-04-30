package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.network.requests.ClearRequest;

public class Clear extends Command {
    final UPDClient client;

    public Clear(UPDClient client) {
        this.client = client;
    }

    /**
     * execute command
     *
     * @param args arguments for command
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    @Override
    public void execute(String[] args) throws WrongAmountOfArgumentsException {
        if (args.length != 0) throw new WrongAmountOfArgumentsException(0, args.length);

        client.sendAndReceive(new ClearRequest(args));
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Clears the collection";
    }
}
