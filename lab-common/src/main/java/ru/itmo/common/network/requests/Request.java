package ru.itmo.common.network.requests;

import java.io.Serializable;

public class Request implements Serializable {
    private final String commandName;
    private final String[] commandArgs;

    public Request(String commandName, String[] commandArgs) {
        this.commandName = commandName;
        this.commandArgs = new String[0];
    }

    public String name() {
        return commandName;
    }

    public String[] arguments() {
        return commandArgs;
    }
}
