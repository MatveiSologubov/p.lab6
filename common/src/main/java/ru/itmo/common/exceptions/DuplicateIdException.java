package ru.itmo.common.exceptions;

/**
 * throws if there is multiple Tickets with same id
 */
public class DuplicateIdException extends RuntimeException {
    public DuplicateIdException(long id) {
        super("ID (" + id + ") is already in use");
    }
}
