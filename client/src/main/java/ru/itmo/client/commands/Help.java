package ru.itmo.client.commands;

import ru.itmo.client.managers.CommandManager;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;

public class Help extends Command {
    final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        this.commandManager = commandManager;
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

        System.out.println("Available commands:");
        commandManager.getAllCommands().forEach((name, cmd) ->
                System.out.printf("  %-27s%s%n", name, cmd.getHelp()));
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Shows this help message";
    }
}
