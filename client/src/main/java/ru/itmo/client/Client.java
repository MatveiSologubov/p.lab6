package ru.itmo.client;

import ru.itmo.client.commands.*;
import ru.itmo.client.managers.CommandManager;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public final class Client {
    final int PORT = 1234;
    final int BUFFER_SIZE = 2048;
    final String HOST = "127.0.0.1";

    final CommandManager commandManager = new CommandManager();
    final Scanner scanner = new Scanner(System.in);
    final ScannerManager scannerManager = new ScannerManager(scanner);
    private UPDClient updClient;

    boolean running = true;

    private Client() {
        try {
            updClient = new UPDClient(HOST, PORT, BUFFER_SIZE);
        } catch (IOException e) {
            System.out.println("Error initializing upd client");
            System.exit(1);
        }

        commandManager.addCommand("help", new Help(commandManager));
        commandManager.addCommand("info", new Info(updClient));
        commandManager.addCommand("show", new Show(updClient));
        commandManager.addCommand("add", new Add(updClient, scannerManager));
        commandManager.addCommand("update", new Update(updClient, scannerManager));
        commandManager.addCommand("remove_by_id", new RemoveById(updClient));
        commandManager.addCommand("clear", new Clear(updClient));
        commandManager.addCommand("execute_script", new ExecuteScript(commandManager, scannerManager));
        commandManager.addCommand("exit", new Exit(this::stop));
        commandManager.addCommand("add_if_max", new AddIfMax(updClient, scannerManager));
        commandManager.addCommand("add_if_min", new AddIfMin(updClient, scannerManager));
        commandManager.addCommand("remove_greater", new RemoveGreater(updClient, scannerManager));
//        commandManager.addCommand("min_by_creation_date", new MinByCreationDate(collectionManager));
//        commandManager.addCommand("filter_less_than_type", new FilterLessThanType(collectionManager));
//        commandManager.addCommand("filter_greater_than_price", new FilterGreaterThanPrice(collectionManager));
    }

    public static void main(String[] args) {
        new Client().start();
    }

    private void stop() {
        running = false;
    }

    private void start() {
        System.out.println("Console program started. Type 'help' for commands.");

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] args = input.split("\\s+");

            Command command = commandManager.getCommand(args[0]);
            args = Arrays.copyOfRange(args, 1, args.length);
            if (command != null) {
                try {
                    command.execute(args);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Unknown command. Type 'help' for available commands");
            }
        }
    }
}
