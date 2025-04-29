package ru.itmo.common.exceptions;

/**
 * throws if element could not be found
 */
public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String message) {
        super("ERROR: " + message + " not found");
    }
}
