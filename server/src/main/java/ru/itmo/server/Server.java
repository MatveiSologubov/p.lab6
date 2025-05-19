package ru.itmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.NullResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Serializer;
import ru.itmo.server.commands.*;
import ru.itmo.server.managers.*;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public final class Server {
    private static final int PORT = 1234;
    private static final int PACKET_SIZE = 2048;
    private final static Logger logger = LogManager.getLogger(Server.class);
    private static String filePath;
    private final CommandManager commandManager = new CommandManager();
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

        networkManager = new NetworkManager(PORT, PACKET_SIZE);

        ConsoleManager consoleManager = new ConsoleManager(this::stop, fileManager, collectionManager, filePath);
        new Thread(consoleManager).start();

        commandManager.addCommand("info", new Info(collectionManager));
        commandManager.addCommand("show", new Show(collectionManager));
        commandManager.addCommand("clear", new Clear(collectionManager));
        commandManager.addCommand("add", new Add(collectionManager));
        commandManager.addCommand("update", new Update(collectionManager));
        commandManager.addCommand("remove_by_id", new RemoveById(collectionManager));
        commandManager.addCommand("add_if_max", new AddIfMax(collectionManager));
        commandManager.addCommand("add_if_min", new AddIfMin(collectionManager));
        commandManager.addCommand("remove_greater", new RemoveGreater(collectionManager));
        commandManager.addCommand("min_by_creation_date", new MinByCreationDate(collectionManager));
        commandManager.addCommand("filter_less_than_type", new FilterLessThanType(collectionManager));
        commandManager.addCommand("filter_greater_than_price", new FilterGreaterThanPrice(collectionManager));
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

            Command command = commandManager.getCommand(request.name());
            Response response = new NullResponse();
            if (command != null) {
                response = command.execute(request);
            }

            sendResponse(response, clientAddress);
        } catch (Exception e) {
            logger.error("Request processing error", e);
        }
    }

    private void sendResponse(Response response, SocketAddress clientAddress) {
        try {
            byte[] data = Serializer.serialize(response);
            final int HEADER_SIZE = 8;
            final int CHUNK_SIZE = PACKET_SIZE - HEADER_SIZE;
            int totalChunks = (int) Math.ceil((double) data.length / CHUNK_SIZE);

            for (int i = 0; i < totalChunks; i++) {
                int offset = i * CHUNK_SIZE;
                int len = Math.min(CHUNK_SIZE, data.length - offset);
                ByteBuffer buf = ByteBuffer.allocate(HEADER_SIZE + len);
                buf.putInt(totalChunks);
                buf.putInt(i);
                buf.put(data, offset, len);
                buf.flip();

                networkManager.send(buf, clientAddress);
                logger.debug("Sent chunk {}/{} to {}", i + 1, totalChunks, clientAddress);
            }
        } catch (Exception e) {
            logger.warn("Failed to send response to {}", clientAddress, e);
        }
    }

    private void shutdown() {
        logger.info("Starting server shutdown");
        fileManager.save(collectionManager.getCollection(), filePath);
        networkManager.close();
        logger.info("Server shutdown completed");
    }
}
