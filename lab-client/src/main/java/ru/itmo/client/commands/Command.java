package ru.itmo.client.commands;


import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;

public abstract class Command {
    /**
     * execute command
     *
     * @param args arguments for command
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    public abstract void execute(String[] args) throws WrongAmountOfArgumentsException;

    /**
     * @return Help message
     */
    public abstract String getHelp();
}
