package ru.itmo.common.exceptions;

/**
 * throws if field is set to empty when it is not allowed
 */
public class FieldMustNotBeEmpty extends Exception {
    public FieldMustNotBeEmpty(String message) {
        super("ERROR: " + message + "must not be empty");
    }
}
