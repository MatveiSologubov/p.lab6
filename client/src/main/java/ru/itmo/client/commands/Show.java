package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.ShowRequest;
import ru.itmo.common.network.responses.ShowResponse;

public class Show extends Command {
    final UPDClient client;

    public Show(UPDClient client) {
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

        ShowResponse response = (ShowResponse) client.sendAndReceive(new ShowRequest());


        if (response.getTickets() == null || response.getTickets().isEmpty()) {
            System.out.println("Collection is empty");
        }

        for (Ticket ticket : response.getTickets()) {
            System.out.println(ticket);
        }
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "";
    }
}
