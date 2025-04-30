package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;

/**
 * This is abstract class for creating commands that have interactions with server
 */
public abstract class NetworkCommand extends Command {
    protected final UPDClient client;

    public NetworkCommand(UPDClient client) {
        this.client = client;
    }
}
