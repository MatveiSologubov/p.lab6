package ru.itmo.server;

import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.NullResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Serializer;
import ru.itmo.server.commands.*;
import ru.itmo.server.managers.CollectionManager;
import ru.itmo.server.managers.CommandManager;
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
    static final int PORT = 1234;
    static final int PACKET_SIZE = 2048;

    static final CommandManager commandManager = new CommandManager();
    static final CollectionManager collectionManager = new CollectionManager();
    static final FileManager fileManager = new FileManager();
    static String filePath;

    private DatagramChannel channel;
    private Selector selector;

    private Server() {
        try {
            channel = DatagramChannel.open();
            InetAddress address = InetAddress.getByName("0.0.0.0");
            channel.bind(new InetSocketAddress(address, PORT));
            System.out.println("Address: " + address);
            channel.configureBlocking(false);

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.out.println("Error initializing server: " + e.getMessage());
        }

        filePath = System.getenv("COLLECTION_FILE");
        if (filePath == null) {
            System.out.println("File path cannot be null");
            System.exit(0);
        }

        collectionManager.setCollection(fileManager.load(filePath));

        commandManager.addCommand("info", new Info(collectionManager));
        commandManager.addCommand("show", new Show(collectionManager));
        commandManager.addCommand("clear", new Clear(collectionManager));
        commandManager.addCommand("add", new Add(collectionManager));
        commandManager.addCommand("update", new Update(collectionManager));
        commandManager.addCommand("remove_by_id", new RemoveById(collectionManager));
        commandManager.addCommand("add_if_max", new AddIfMax(collectionManager));
        commandManager.addCommand("add_if_min", new AddIfMin(collectionManager));
        commandManager.addCommand("remove_greater", new RemoveGreater(collectionManager));
    }

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        System.out.println("Server started on port " + PORT);
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);

        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (!key.isReadable()) continue;

                    buffer.clear();
                    SocketAddress clientAddress = channel.receive(buffer);
                    System.out.println("Client " + clientAddress + " received");

                    if (clientAddress == null) continue;

                    handleRequest(buffer, clientAddress);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleRequest(ByteBuffer buffer, SocketAddress clientAddress) {
        try {
            buffer.flip();
            byte[] trimmedData = Arrays.copyOf(buffer.array(), buffer.limit());

            Request request = (Request) Serializer.deserialize(trimmedData);
            Command command = commandManager.getCommand(request.name());


            Response response = new NullResponse();
            if (command != null) {
                response = command.execute(request);
            }

            sendResponse(response, clientAddress);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void sendResponse(Response response, SocketAddress clientAddress) {
        try {
            byte[] data = Serializer.serialize(response);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            channel.send(buffer, clientAddress);
        } catch (IOException e) {
            System.out.println("Error sending response: " + e.getMessage());
        }
    }
}
