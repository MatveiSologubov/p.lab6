package ru.itmo.common.exceptions;

/**
 * throws if script recursion is detected
 */
public class ScriptRecursionException extends Exception {
    public ScriptRecursionException() {
        super("ERROR: Script recursion detected");
    }
}
