package ru.itmo.client.commands;

import ru.itmo.client.builders.TicketBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddRequest;
import ru.itmo.common.network.responses.AddResponse;

import java.io.IOException;

/**
 * 'Add' command builds and adds Ticket to collection if its valid
 */
public class Add extends NetworkCommand {
    final ScannerManager scannerManager;

    public Add(UPDClient client, ScannerManager scannerManager) {
        super(client);
        this.scannerManager = scannerManager;
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

        TicketBuilder builder = new TicketBuilder(scannerManager.getScanner());
        Ticket ticket = builder.build();

        AddResponse response = (AddResponse) client.sendAndReceive(new AddRequest(ticket));

        if (response.isSuccess()) {
            System.out.println("Ticket added successfully");
        } else {
            System.out.println("Ticket could not be added");
        }
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Adds element to collection; Usage add {element}";
    }
}
