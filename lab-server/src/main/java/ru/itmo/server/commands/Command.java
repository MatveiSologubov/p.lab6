package ru.itmo.server.commands;


import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;

public abstract class Command {
    /**
     * execute command
     *
     * @param request request from client
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    public abstract Response execute(Request request) throws WrongAmountOfArgumentsException;

    /**
     * @return Help message
     */
    public abstract String getHelp();
}
