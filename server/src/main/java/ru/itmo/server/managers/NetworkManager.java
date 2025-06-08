package ru.itmo.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Config;
import ru.itmo.common.util.Serializer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Manages network communication using UDP.
 * Handles receiving requests and sending responses in chunks.
 */
public class NetworkManager {
    private static final Logger logger = LogManager.getLogger(NetworkManager.class);

    private final DatagramChannel channel;
    private final Selector selector;
    private final int BUFFER_SIZE;
    private final int HEADER_SIZE;

    public NetworkManager(Config config) throws IOException {
        channel = DatagramChannel.open();
        channel.bind(new java.net.InetSocketAddress("0.0.0.0", config.getPort()));
        channel.configureBlocking(false);

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        this.BUFFER_SIZE = config.getBufferSize();
        this.HEADER_SIZE = config.getHeaderSize();

        logger.info("Server started on {}", channel.getLocalAddress());
    }

    /**
     * Receives data in chunks
     *
     * @param timeoutMs timeout after witch we will stop receiving
     * @return received data
     * @throws IOException if encounters one
     */
    public Received receive(int timeoutMs) throws IOException {
        if (selector.select(timeoutMs) == 0) return null;

        for (SelectionKey key : selector.selectedKeys()) {
            selector.selectedKeys().remove(key);
            if (!key.isValid() || !key.isReadable()) continue;

            ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
            SocketAddress addr = channel.receive(buf);
            if (addr == null) continue;

            buf.flip();
            logger.debug("Received request from {}", addr);
            return new Received(buf, addr);
        }

        return null;
    }

    /**
     * Sends response to client
     *
     * @param response      response to send
     * @param clientAddress address to send response
     */
    public void sendResponse(Response response, SocketAddress clientAddress) {
        try {
            byte[] data = Serializer.serialize(response);
            final int CHUNK_SIZE = BUFFER_SIZE - HEADER_SIZE;
            int totalChunks = (int) Math.ceil((double) data.length / CHUNK_SIZE);

            for (int i = 0; i < totalChunks; i++) {
                int offset = i * CHUNK_SIZE;
                int len = Math.min(CHUNK_SIZE, data.length - offset);
                ByteBuffer buf = ByteBuffer.allocate(HEADER_SIZE + len);
                buf.putInt(totalChunks);
                buf.putInt(i);
                buf.put(data, offset, len);
                buf.flip();

                send(buf, clientAddress);
                logger.debug("Sent chunk {}/{} to {}", i + 1, totalChunks, clientAddress);
            }
        } catch (Exception e) {
            logger.warn("Failed to send response to {}", clientAddress, e);
        }
    }

    /**
     * Sends buffer to address
     *
     * @param buffer     buffer to send
     * @param clientAddr address of the client
     * @throws IOException if encounters one
     */
    private void send(ByteBuffer buffer, SocketAddress clientAddr) throws IOException {
        channel.send(buffer, clientAddr);
        logger.debug("Sent {} bytes to {}", buffer.remaining(), clientAddr);
    }

    /**
     * Closes channel and selector when shutting down server
     */
    public void close() {
        try {
            channel.close();
            selector.close();
            logger.debug("NetworkManager: channel and selector closed");
        } catch (IOException e) {
            logger.error("Error closing NetworkManager", e);
        }
    }

    /**
     * Inner class which represent received data
     */
    public static class Received {
        public final ByteBuffer buffer;
        public final SocketAddress clientAddress;

        public Received(ByteBuffer buffer, SocketAddress clientAddress) {
            this.buffer = buffer;
            this.clientAddress = clientAddress;
        }
    }
}
