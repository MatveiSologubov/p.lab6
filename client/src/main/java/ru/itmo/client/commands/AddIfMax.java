package ru.itmo.client.commands;

import ru.itmo.client.builders.TicketBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMaxRequest;
import ru.itmo.common.network.responses.AddIfMaxResponse;

/**
 * 'Add If Max' commands builds and adds element to collection if its valid and has maximum price
 */
public class AddIfMax extends NetworkCommand {
    private final ScannerManager scannerManager;

    public AddIfMax(UPDClient updClient, ScannerManager scannerManager) {
        super(updClient);
        this.scannerManager = scannerManager;
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

        TicketBuilder builder = new TicketBuilder(scannerManager.getScanner());
        Ticket ticket = builder.build();

        AddIfMaxResponse response = (AddIfMaxResponse) client.sendAndReceive(new AddIfMaxRequest(ticket));

        System.out.println(response.getMessage());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Adds element to collection; Usage add {element}";
    }
}
