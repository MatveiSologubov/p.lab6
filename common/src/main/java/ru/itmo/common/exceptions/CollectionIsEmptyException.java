package ru.itmo.common.exceptions;

/**
 * Throws if collection is empty
 */
public class CollectionIsEmptyException extends Exception {
    public CollectionIsEmptyException() {
        super("ERROR: Collection is empty");
    }
}
