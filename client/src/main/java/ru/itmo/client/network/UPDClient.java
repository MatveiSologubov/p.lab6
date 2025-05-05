package ru.itmo.client.network;

import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UPDClient {
    private final int PORT;
    private final int BUFFER_SIZE;
    private final int TIMEOUT = 3000;

    private final DatagramSocket socket;
    private final InetAddress serverAddress;

    public UPDClient(String host, int port, int bufferSize) throws IOException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(TIMEOUT);
        this.serverAddress = InetAddress.getByName(host);
        this.PORT = port;
        this.BUFFER_SIZE = bufferSize;
    }

    public Response sendAndReceive(Request request) throws IOException {
        byte[] data = Serializer.serialize(request);
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, PORT);
        socket.send(packet);

        byte[] receive = new byte[BUFFER_SIZE];
        packet = new DatagramPacket(receive, BUFFER_SIZE);
        socket.receive(packet);

        return (Response) Serializer.deserialize(receive);
    }
}
