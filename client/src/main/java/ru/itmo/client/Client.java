package ru.itmo.client;

import ru.itmo.client.commands.*;
import ru.itmo.client.managers.CommandRegistry;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public final class Client {
    final int PORT = 1234;
    final int BUFFER_SIZE = 2048;
    final String HOST = "127.0.0.1";

    final CommandRegistry commandRegistry;
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

        commandRegistry = new CommandRegistry() {{
            registerCommand("help", new Help(this));
            registerCommand("info", new Info(updClient));
            registerCommand("show", new Show(updClient));
            registerCommand("add", new Add(updClient, scannerManager));
            registerCommand("update", new Update(updClient, scannerManager));
            registerCommand("remove_by_id", new RemoveById(updClient));
            registerCommand("clear", new Clear(updClient));
            registerCommand("execute_script", new ExecuteScript(this, scannerManager));
            registerCommand("exit", new Exit(Client.this::stop));
            registerCommand("add_if_max", new AddIfMax(updClient, scannerManager));
            registerCommand("add_if_min", new AddIfMin(updClient, scannerManager));
            registerCommand("remove_greater", new RemoveGreater(updClient, scannerManager));
            registerCommand("min_by_creation_date", new MinByCreationDate(updClient));
            registerCommand("filter_less_than_type", new FilterLessThanType(updClient));
            registerCommand("filter_greater_than_price", new FilterGreaterThanPrice(updClient));
        }};
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

            Command command = commandRegistry.getCommand(args[0]);
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
