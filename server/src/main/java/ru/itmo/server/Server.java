package ru.itmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.NullResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Config;
import ru.itmo.common.util.Serializer;
import ru.itmo.server.commands.*;
import ru.itmo.server.managers.*;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * Server application entry point.
 * Initializes managers, handles shutdown hooks, and processes client requests.
 */
public final class Server {
    private final static Logger logger = LogManager.getLogger(Server.class);
    private static String filePath;
    private final CommandRegistry commandRegistry;
    private final CollectionManager collectionManager = new CollectionManager();
    private final NetworkManager networkManager;
    private final FileManager fileManager = new FileManager();

    private volatile boolean running = true;

    private Server() throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered");
            fileManager.save(collectionManager.getCollection(), filePath);
        }));

        logger.info("Initializing server");

        filePath = System.getenv("COLLECTION_FILE");
        if (filePath == null) {
            System.out.println("File path cannot be null");
            System.exit(0);
        }

        collectionManager.setCollection(fileManager.load(filePath));

        networkManager = new NetworkManager(Config.defaultConfig());

        ConsoleManager consoleManager = new ConsoleManager(this::stop, fileManager, collectionManager, filePath);
        new Thread(consoleManager).start();

        commandRegistry = new CommandRegistry() {{
            registerCommand("info", new Info(collectionManager));
            registerCommand("show", new Show(collectionManager));
            registerCommand("clear", new Clear(collectionManager));
            registerCommand("add", new Add(collectionManager));
            registerCommand("update", new Update(collectionManager));
            registerCommand("remove_by_id", new RemoveById(collectionManager));
            registerCommand("add_if_max", new AddIfMax(collectionManager));
            registerCommand("add_if_min", new AddIfMin(collectionManager));
            registerCommand("remove_greater", new RemoveGreater(collectionManager));
            registerCommand("min_by_creation_date", new MinByCreationDate(collectionManager));
            registerCommand("filter_less_than_type", new FilterLessThanType(collectionManager));
            registerCommand("filter_greater_than_price", new FilterGreaterThanPrice(collectionManager));
        }};
    }

    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (IOException e) {
            logger.error("Server initialization failed", e);
            System.exit(1);
        }
    }

    private void stop() {
        running = false;
    }

    public void start() {
        logger.info("Starting server main loop");
        try {
            while (running) {
                NetworkManager.Received rec = networkManager.receive(100);
                if (rec == null) {
                    if (!running) break;
                    continue;
                }

                handleRequest(rec.buffer, rec.clientAddress);
            }
        } catch (Exception e) {
            logger.error("Critical server error", e);
        } finally {
            shutdown();
        }
    }

    private void handleRequest(ByteBuffer buffer, SocketAddress clientAddress) {
        try {
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);

            Request request = (Request) Serializer.deserialize(data);
            logger.info("Processing command '{}' from {}", request.name(), clientAddress);

            Command command = commandRegistry.getCommand(request.name());
            Response response = new NullResponse();
            if (command != null) {
                response = command.execute(request);
            }

            networkManager.sendResponse(response, clientAddress);
        } catch (Exception e) {
            logger.error("Request processing error", e);
        }
    }


    private void shutdown() {
        logger.info("Starting server shutdown");
        fileManager.save(collectionManager.getCollection(), filePath);
        networkManager.close();
        logger.info("Server shutdown completed");
    }
}
