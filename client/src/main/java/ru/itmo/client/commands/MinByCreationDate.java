package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.network.requests.MinByCreationDateRequest;
import ru.itmo.common.network.responses.MinByCreationDateResponse;

import java.io.IOException;

/**
 * 'Min By Creation Date' command print Ticket with minimum creation date
 */
public class MinByCreationDate extends NetworkCommand {
    public MinByCreationDate(UPDClient client) {
        super(client);
    }

    /**
     * execute command
     *
     * @param args arguments for command
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    @Override
    public void execute(String[] args) throws WrongAmountOfArgumentsException, IOException {
        if (args.length != 0) throw new WrongAmountOfArgumentsException(0, args.length);

        MinByCreationDateRequest request = new MinByCreationDateRequest();
        MinByCreationDateResponse response = (MinByCreationDateResponse) client.sendAndReceive(request);

        if (response.isSuccess()) {
            System.out.println(response.getTicket());
            return;
        }

        System.out.println(response.getErrorMessage());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Shows element with minimum creation date";
    }
}
