package ru.itmo.server.managers;

import java.util.Scanner;

/**
 * Manages different scanners that are used in program
 */
public class ScannerManager {
    private final Scanner mainScanner;
    boolean scriptMode = false;
    private Scanner fileScanner;

    /**
     * Initialises scanner manager
     *
     * @param mainScanner scanner to use in interactive mode
     */
    public ScannerManager(Scanner mainScanner) {
        this.mainScanner = mainScanner;
    }

    /**
     * Set scanner for script mode
     *
     * @param fileScanner scanner to set
     */
    public void setScriptScanner(Scanner fileScanner) {
        this.fileScanner = fileScanner;
    }

    /**
     * Set script mode
     *
     * @param scriptMode true for script mode; false for interactive mode
     */
    public void setScriptMode(boolean scriptMode) {
        this.scriptMode = scriptMode;
    }

    /**
     * Gets scanner that are currently used
     *
     * @return active scanner
     */
    public Scanner getScanner() {
        if (scriptMode) {
            return fileScanner;
        }
        return mainScanner;
    }
}
