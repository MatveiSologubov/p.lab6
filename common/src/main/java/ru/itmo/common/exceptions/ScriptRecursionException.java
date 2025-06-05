package ru.itmo.common.exceptions;

/**
 * thrown if script recursion is detected
 */
public class ScriptRecursionException extends Exception {
    public ScriptRecursionException() {
        super("ERROR: Script recursion detected");
    }
}
