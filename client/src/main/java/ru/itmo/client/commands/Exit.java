package ru.itmo.client.commands;


import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;

/**
 * 'Exit' command exits the application
 */
public class Exit extends Command {
    private final Runnable exitHandler;

    public Exit(Runnable exitHandler) {
        this.exitHandler = exitHandler;
    }

    /**
     * @param args arguments for command
     * @throws WrongAmountOfArgumentsException if user puts incorrect amount of arguments
     */
    @Override
    public void execute(String[] args) throws WrongAmountOfArgumentsException {
        if (args.length != 0) throw new WrongAmountOfArgumentsException(0, args.length);

        System.out.println("Exiting program...");
        exitHandler.run();
    }

    @Override
    public String getHelp() {
        return "Exits the program";
    }
}
