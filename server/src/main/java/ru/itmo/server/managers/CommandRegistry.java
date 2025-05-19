package ru.itmo.server.managers;

import ru.itmo.server.commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to store and get commands
 */
public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }
}
