package ru.itmo.server;

import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.NullResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Serializer;
import ru.itmo.server.commands.Clear;
import ru.itmo.server.commands.Command;
import ru.itmo.server.commands.Info;
import ru.itmo.server.commands.Show;
import ru.itmo.server.managers.CollectionManager;
import ru.itmo.server.managers.CommandManager;
import ru.itmo.server.managers.FileManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public final class Server {
    static final int PORT = 1234;
    static final int PACKET_SIZE = 2048;
    static final CommandManager commandManager = new CommandManager();
    static final CollectionManager collectionManager = new CollectionManager();
    static final FileManager fileManager = new FileManager();
    static String filePath;
    DatagramSocket socket;

    private Server() {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            throw new RuntimeException(e);
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
    }

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        while (true) {
            DatagramPacket packet = receive();
            byte[] trimmedData = Arrays.copyOf(packet.getData(), packet.getLength());
            Request request = (Request) Serializer.deserialize(trimmedData);
            Command command = commandManager.getCommand(request.name());

            if (command == null) {
                sendData(new NullResponse(), packet.getAddress(), packet.getPort());
                continue;
            }

            sendData(command.execute(request), packet.getAddress(), packet.getPort());
        }
    }

    public DatagramPacket receive() {
        byte[] buffer = new byte[PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return packet;
    }

    public void sendData(Response response, InetAddress host, int port) {
        byte[] arr = Serializer.serialize(response);

        DatagramPacket packet = new DatagramPacket(arr, arr.length, host, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
