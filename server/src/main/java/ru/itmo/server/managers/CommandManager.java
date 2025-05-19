package ru.itmo.server.managers;

import ru.itmo.server.commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to store and get commands
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public void addCommand(String name, Command command) {
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }
}
