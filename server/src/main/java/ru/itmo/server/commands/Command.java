package ru.itmo.server.commands;


import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;

public abstract class Command {
    /**
     * execute command
     *
     * @param request request from client
     */
    public abstract Response execute(Request request);

    /**
     * @return Help message
     */
    public abstract String getHelp();
}
