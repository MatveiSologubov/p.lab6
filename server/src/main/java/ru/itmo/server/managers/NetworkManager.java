package ru.itmo.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Config;
import ru.itmo.common.util.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
     * Receives data in chunks from client
     *
     * @param timeoutMs timeout after which we will stop receiving
     * @return received data
     * @throws IOException if encounters one
     */
    public Received receive(int timeoutMs) throws IOException {
        long deadline = System.currentTimeMillis() + timeoutMs;

        Map<SocketAddress, TreeMap<Integer, byte[]>> pending = new HashMap<>();
        Map<SocketAddress, Integer> expectedTotals = new HashMap<>();

        while (true) {
            long remainingTime = deadline - System.currentTimeMillis();
            if (remainingTime <= 0 || selector.select(remainingTime) == 0) {
                return null;
            }

            for (SelectionKey key : selector.selectedKeys()) {
                selector.selectedKeys().remove(key);
                if (!key.isValid() || !key.isReadable()) continue;

                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                SocketAddress addr = channel.receive(buffer);
                if (addr == null) continue;
                buffer.flip();

                int totalNumberOfChunks = buffer.getInt();
                int index = buffer.getInt();

                byte[] chunk = new byte[buffer.remaining()];
                buffer.get(chunk);

                pending.computeIfAbsent(addr, k -> new TreeMap<>()).put(index, chunk);
                logger.debug("Received chunk {}/{} from {}", index + 1, totalNumberOfChunks, addr);

                expectedTotals.putIfAbsent(addr, totalNumberOfChunks);

                if (pending.get(addr).size() == expectedTotals.get(addr)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    for (byte[] part : pending.get(addr).values()) {
                        out.write(part);
                    }
                    ByteBuffer full = ByteBuffer.wrap(out.toByteArray());
                    logger.debug("Received request from {}", addr);
                    return new Received(full, addr);
                }
            }
        }
    }

    /**
     * Sends response to client in chunks
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
        logger.debug("Sent {} bytes to {}", buffer.capacity(), clientAddr);
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
