package ru.itmo.common.util;

/**
 * Stores network configuration parameters
 */
public final class Config {
    private final String host;
    private final int port;
    private final int bufferSize;
    private final int headerSize;
    private final int timeoutMs;

    public Config(String host, int port, int bufferSize, int headerSize, int timeoutMs) {
        this.host = host;
        this.port = port;
        this.bufferSize = bufferSize;
        this.headerSize = headerSize;
        this.timeoutMs = timeoutMs;
    }

    public static Config defaultConfig() {
        return new Config("192.168.10.80", 3668, 1024, 8, 3000);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }
}
