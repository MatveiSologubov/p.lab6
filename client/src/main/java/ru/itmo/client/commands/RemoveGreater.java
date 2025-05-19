package ru.itmo.client.commands;

import ru.itmo.client.builders.TicketBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.RemoveGreaterRequest;
import ru.itmo.common.network.responses.RemoveGreaterResponse;

import java.io.IOException;

/**
 * 'Remove Greater' command builds ticket and then removes all tickets with bigger price than built ticket
 */
public class RemoveGreater extends NetworkCommand {
    private final ScannerManager scannerManager;

    public RemoveGreater(UPDClient client, ScannerManager scannerManager) {
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

        RemoveGreaterRequest request = new  RemoveGreaterRequest(ticket);
        RemoveGreaterResponse response;
        response = (RemoveGreaterResponse) client.sendAndReceive(request);

        System.out.println(response.getMessage());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Removes all tickets bigger than specified element";
    }
}
