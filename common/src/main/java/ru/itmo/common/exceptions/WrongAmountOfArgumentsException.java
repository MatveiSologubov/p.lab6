package ru.itmo.common.exceptions;

/**
 * thrown if command is provided with wrong amount of arguments
 */
public class WrongAmountOfArgumentsException extends Exception {
    public WrongAmountOfArgumentsException(int expected, int actual) {
        super("ERROR: Expected " + expected + " arguments but got " + actual);
    }
}
