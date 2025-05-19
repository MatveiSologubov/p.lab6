package ru.itmo.client.network;

import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Config;
import ru.itmo.common.util.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class UPDClient {
    private final int PORT;
    private final int BUFFER_SIZE;
    private final int TIMEOUT;
    private final int HEADER_SIZE;

    private final DatagramSocket socket;
    private final InetAddress serverAddress;

    public UPDClient(Config config) throws IOException {
        this.socket = new DatagramSocket();
        this.TIMEOUT = config.getTimeoutMs();
        this.socket.setSoTimeout(TIMEOUT);
        this.serverAddress = InetAddress.getByName(config.getHost());
        this.PORT = config.getPort();
        this.BUFFER_SIZE = config.getBufferSize();
        this.HEADER_SIZE = config.getHeaderSize();
    }

    public Response sendAndReceive(Request request) throws IOException {
        byte[] data = Serializer.serialize(request);
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, PORT);
        socket.send(packet);

        Map<Integer, byte[]> chunks = new TreeMap<>();
        int totalNumberOfChunks = -1;
        long startTime = System.currentTimeMillis();

        do {
            if (System.currentTimeMillis() - startTime > TIMEOUT) {
                throw new IOException("Timeout while waiting for chunks");
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            byte[] receivedData = Arrays.copyOf(packet.getData(), packet.getLength());

            ByteBuffer byteBuffer = ByteBuffer.wrap(receivedData);
            int chunkTotal = byteBuffer.getInt();
            int chunkIndex = byteBuffer.getInt();
            byte[] chunk = new byte[receivedData.length - HEADER_SIZE];
            byteBuffer.get(chunk);

            if (totalNumberOfChunks == -1) {
                totalNumberOfChunks = chunkTotal;
            }

            chunks.put(chunkIndex, chunk);

        } while (totalNumberOfChunks == -1 || chunks.size() < totalNumberOfChunks);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (byte[] chunk : chunks.values()) {
            outputStream.write(chunk, 0, chunk.length);
        }
        byte[] response = outputStream.toByteArray();
        return (Response) Serializer.deserialize(response);
    }
}
