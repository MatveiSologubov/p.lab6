package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.FilterGreaterThanPriceRequest;
import ru.itmo.common.network.responses.FilterLessThanTypeResponse;

import java.io.IOException;

/**
 * 'Filter Greater Than Price' command prints every Ticket which has price greater than specified
 */
public class FilterGreaterThanPrice extends NetworkCommand {
    public FilterGreaterThanPrice(UPDClient updClient) {
        super(updClient);
    }

    /**
     * execute command
     *
     * @param args price to filter
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    @Override
    public void execute(String[] args) throws WrongAmountOfArgumentsException, IOException {
        if (args.length != 1) throw new WrongAmountOfArgumentsException(1, args.length);

        float price;
        try {
            price = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Wrong number format");
            return;
        }

        FilterGreaterThanPriceRequest request = new FilterGreaterThanPriceRequest(price);
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
        return "Prints all the tickets that have price greater than the specified price.";
    }
}
