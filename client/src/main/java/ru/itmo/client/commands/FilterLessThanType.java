package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.models.TicketType;
import ru.itmo.common.network.requests.FilterLessThanTypeRequest;
import ru.itmo.common.network.responses.FilterLessThanTypeResponse;

import java.io.IOException;
import java.util.Arrays;

/**
 * 'Filter Less Than Type' prints all ticket which have TicketType less than specified
 */
public class FilterLessThanType extends NetworkCommand {
    public FilterLessThanType(UPDClient client) {
        super(client);
    }

    /**
     * execute command
     *
     * @param args TicketType to filter
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    @Override
    public void execute(String[] args) throws WrongAmountOfArgumentsException, IOException {
        if (args.length != 1) throw new WrongAmountOfArgumentsException(1, args.length);
        System.out.println("Order: " + TicketType.order());

        TicketType type;
        try {
            type = TicketType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Wrong type format");
            System.out.println("Allowed types are: " + Arrays.toString(TicketType.values()));
            return;
        }


        FilterLessThanTypeRequest request = new FilterLessThanTypeRequest(type);
        FilterLessThanTypeResponse response = (FilterLessThanTypeResponse) client.sendAndReceive(request);

        if (!response.isSuccess()) {
            System.out.println(response.getMessage());
            return;
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
        return "Prints all the tickets that have type \"smaller\" than the specified type.";
    }
}