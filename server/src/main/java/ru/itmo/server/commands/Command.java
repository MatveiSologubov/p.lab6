package ru.itmo.server.commands;


import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;

/**
 * Abstract command that all others are based on
 */
public abstract class Command {
    /**
     * execute command
     *
     * @param request request from client
     */
    public abstract Response execute(Request request);
}
