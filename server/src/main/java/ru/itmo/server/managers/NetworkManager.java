package ru.itmo.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class NetworkManager {
    private static final Logger logger = LogManager.getLogger(NetworkManager.class);

    private final DatagramChannel channel;
    private final Selector selector;
    private final int packetSize;

    public NetworkManager(int port, int packetSize) throws IOException {
        this.packetSize = packetSize;
        channel = DatagramChannel.open();
        channel.bind(new java.net.InetSocketAddress("0.0.0.0", port));
        channel.configureBlocking(false);

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        logger.info("Server started on port {}", port);
    }

    public Received receive(int timeoutMs) throws IOException {
        if (selector.select(timeoutMs) == 0) return null;

        for (SelectionKey key : selector.selectedKeys()) {
            selector.selectedKeys().remove(key);
            if (!key.isValid() || !key.isReadable()) continue;

            ByteBuffer buf = ByteBuffer.allocate(packetSize);
            SocketAddress addr = channel.receive(buf);
            if (addr == null) continue;

            buf.flip();
            logger.debug("Received request from {}", addr);
            return new Received(buf, addr);
        }
        return null;
    }

    public void send(ByteBuffer buffer, SocketAddress clientAddr) throws IOException {
        channel.send(buffer, clientAddr);
        logger.debug("Sent {} bytes to {}", buffer.remaining(), clientAddr);
    }

    public void close() {
        try {
            channel.close();
            selector.close();
            logger.debug("NetworkManager: channel and selector closed");
        } catch (IOException e) {
            logger.error("Error closing NetworkManager", e);
        }
    }

    public static class Received {
        public final ByteBuffer buffer;
        public final SocketAddress clientAddress;

        public Received(ByteBuffer buffer, SocketAddress clientAddress) {
            this.buffer = buffer;
            this.clientAddress = clientAddress;
        }
    }
}
