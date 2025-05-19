package ru.itmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.NullResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Serializer;
import ru.itmo.server.commands.*;
import ru.itmo.server.managers.CollectionManager;
import ru.itmo.server.managers.CommandManager;
import ru.itmo.server.managers.ConsoleManager;
import ru.itmo.server.managers.FileManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public final class Server {
    private static final int PORT = 1234;
    private static final int PACKET_SIZE = 2048;
    private final static Logger logger = LogManager.getLogger(Server.class);
    private static String filePath;
    private final CommandManager commandManager = new CommandManager();
    private final CollectionManager collectionManager = new CollectionManager();
    private final FileManager fileManager = new FileManager();
    private DatagramChannel channel;
    private Selector selector;

    private volatile boolean running = true;

    private Server() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook triggered");
            fileManager.save(collectionManager.getCollection(), filePath);
        }));

        try {
            logger.info("Initializing server");
            channel = DatagramChannel.open();
            InetAddress address = InetAddress.getByName("0.0.0.0");
            channel.bind(new InetSocketAddress(address, PORT));
            channel.configureBlocking(false);

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            logger.info("Server started on address {} and port {}", address, PORT);
        } catch (IOException e) {
            logger.error("Server initialization failed", e);
            System.exit(1);
        }

        filePath = System.getenv("COLLECTION_FILE");
        if (filePath == null) {
            System.out.println("File path cannot be null");
            System.exit(0);
        }


        collectionManager.setCollection(fileManager.load(filePath));

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
        new Server().start();
    }

    public void start() {
        logger.info("Starting sever main loop");

        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);

        try {
            while (running) {
                if (selector.select(100) == 0) {
                    if (!running) break;
                    continue;
                }
                processSelectedKeys(buffer);
            }
        } catch (IOException e) {
            logger.error("Critical sever error", e);
        } finally {
            shutdown();
        }
    }

    public void stop() {
        running = false;
    }

    private void shutdown() {
        logger.info("Starting server shutdown");

        fileManager.save(collectionManager.getCollection(), filePath);

        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                logger.debug("DatagramChanel closed");
            }
        } catch (IOException e) {
            logger.error("Error closing channel", e);
        }

        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
                logger.debug("Selector closed");
            }
        } catch (IOException e) {
            logger.error("Error closing selector", e);
        }

        logger.info("Server shutdown completed");
    }

    private void processSelectedKeys(ByteBuffer buffer) throws IOException {
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (!key.isValid()) {
                logger.warn("Invalid selection key {}", key);
                continue;
            }

            if (!key.isReadable()) continue;

            processReadableKey(buffer);
        }
    }

    private void processReadableKey(ByteBuffer buffer) throws IOException {
        buffer.clear();
        SocketAddress clientAddress = channel.receive(buffer);

        if (clientAddress == null) return;

        logger.debug("Received request from {}", clientAddress);
        handleRequest(buffer, clientAddress);
    }

    private void handleRequest(ByteBuffer buffer, SocketAddress clientAddress) {
        try {
            buffer.flip();
            byte[] trimmedData = Arrays.copyOf(buffer.array(), buffer.limit());

            Request request = (Request) Serializer.deserialize(trimmedData);
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
            int chunkSize = PACKET_SIZE - HEADER_SIZE;
            int totalNumberOfChunks = (int) Math.ceil((double) data.length / chunkSize);

            for (int i = 0; i < totalNumberOfChunks; i++) {
                int offset = i * chunkSize;
                int chunkLength = Math.min(chunkSize, data.length - offset);
                byte[] chunk = Arrays.copyOfRange(data, offset, offset + chunkLength);

                ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + chunkLength);
                buffer.putInt(totalNumberOfChunks);
                buffer.putInt(i);
                buffer.put(chunk);
                buffer.flip();

                channel.send(buffer, clientAddress);
                logger.debug("Sent chunk {}/{} to {}", i + 1, totalNumberOfChunks, clientAddress);
            }

        } catch (IOException e) {
            logger.warn("Failed to send response to {}", clientAddress, e);
        }
    }
}
