package ru.itmo.client.network;

import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UPDClient {
    private final String HOST;
    private final int PORT;
    private final int BUFFER_SIZE;

    public UPDClient(String host, int port, int bufferSize) {
        this.HOST = host;
        this.PORT = port;
        this.BUFFER_SIZE = bufferSize;
    }

    public Response sendAndReceive(Request request) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress host = InetAddress.getByName(HOST);

            byte[] data = Serializer.serialize(request);
            DatagramPacket packet = new DatagramPacket(data, data.length, host, PORT);
            socket.send(packet);

            byte[] receive = new byte[BUFFER_SIZE];
            packet = new DatagramPacket(receive, BUFFER_SIZE);
            socket.receive(packet);

            return (Response) Serializer.deserialize(receive);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
