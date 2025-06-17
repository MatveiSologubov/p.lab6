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

/**
 * UDP network client for server communication
 */
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

    /**
     * Sends request to server and then receives response
     *
     * @param request request to send
     * @return response from server
     * @throws IOException if timeout reached or socket throws one
     */
    public Response sendAndReceive(Request request) throws IOException {
        byte[] data = Serializer.serialize(request);
        sendDataInChunks(data);

        byte[] receivedData = receiveDataInChunks();
        return (Response) Serializer.deserialize(receivedData);
    }

    /**
     * Sends byte array to server
     *
     * @param data byte array to send to server
     * @throws IOException if socket throws one
     */
    private void sendDataInChunks(byte[] data) throws IOException {
        final int CHUNK_SIZE = BUFFER_SIZE - HEADER_SIZE;
        int totalChunks = (int) Math.ceil((double) data.length / CHUNK_SIZE);

        for (int i = 0; i < totalChunks; i++) {
            int offset = i * CHUNK_SIZE;
            int currentLength = Math.min(CHUNK_SIZE, data.length - offset);

            ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + currentLength);
            buffer.putInt(totalChunks);
            buffer.putInt(i);
            buffer.put(data, offset, currentLength);
            buffer.flip();

            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.limit(), serverAddress, PORT);
            socket.send(packet);
        }
    }

    /**
     * Receives data in chunks from server
     *
     * @return byte array received from server
     * @throws IOException if reaches timeout
     */
    private byte[] receiveDataInChunks() throws IOException {
        Map<Integer, byte[]> chunks = new TreeMap<>();
        int totalNumberOfChunks = -1;
        long startTime = System.currentTimeMillis();

        do {
            if (System.currentTimeMillis() - startTime > TIMEOUT) {
                throw new IOException("Timeout while waiting for chunks");
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
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
        return outputStream.toByteArray();
    }
}
