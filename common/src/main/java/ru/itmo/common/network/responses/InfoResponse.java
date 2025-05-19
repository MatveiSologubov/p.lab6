package ru.itmo.common.network.responses;

import java.time.LocalDateTime;

public class InfoResponse extends Response {
    private final String collectionType;
    private final int collectionSize;
    private final LocalDateTime initTime;

    public InfoResponse(String collectionType, int collectionSize, LocalDateTime initTime) {
        this.collectionType = collectionType;
        this.collectionSize = collectionSize;
        this.initTime = initTime;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public int getCollectionSize() {
        return collectionSize;
    }

    public LocalDateTime getInitTime() {
        return initTime;
    }
}
