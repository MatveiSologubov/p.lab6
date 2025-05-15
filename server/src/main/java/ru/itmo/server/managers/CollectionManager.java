package ru.itmo.server.managers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.exceptions.DuplicateIdException;
import ru.itmo.common.models.Ticket;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages collection
 */
public class CollectionManager {
    private final Set<Long> usedIds = new HashSet<>();
    private final LocalDateTime initTime;
    private final Set<Ticket> collection = new HashSet<>();

    private final static Logger logger = LogManager.getLogger(CollectionManager.class);

    public CollectionManager() {
        this.initTime = LocalDateTime.now();
    }

    /**
     * Add ticket to collection if its unique
     *
     * @param ticket ticket to add
     * @throws DuplicateIdException if id already used
     */
    public void add(Ticket ticket) throws DuplicateIdException {
        final long id = ticket.getId();
        if (usedIds.contains(id)) {
            throw new DuplicateIdException(id);
        }
        usedIds.add(id);
        collection.add(ticket);
    }

    /**
     * Clears collection
     */
    public void clearCollection() {
        collection.clear();
    }

    public int getCollectionSize() {
        return collection.size();
    }

    public Set<Ticket> getCollection() {
        return this.collection;
    }

    /**
     * Set collection
     *
     * @param newCollection collection to add
     */
    public void setCollection(Set<Ticket> newCollection) {
        collection.clear();
        usedIds.clear();
        long maxId = 0;
        for (Ticket ticket : newCollection) {
            try {
                add(ticket);
                maxId = Math.max(maxId, ticket.getId());
            } catch (DuplicateIdException e) {
                logger.warn("Duplicate id detected: {}", e.getMessage());
            }
        }
        Ticket.setIdCounter(maxId + 1);
    }

    public LocalDateTime getInitTime() {
        return this.initTime;
    }

    public String getCollectionType() {
        return this.collection.getClass().getSimpleName();
    }
}
