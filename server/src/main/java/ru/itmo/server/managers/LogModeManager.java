package ru.itmo.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Controls console logging verbosity at runtime.
 * Dynamically adjusts log4j2 configuration.
 */
public class LogModeManager {
    private static final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

    /**
     * Enables console logging
     */
    public static void enableConsoleLogging() {
        System.setProperty("consoleLogLevel", "ALL");
        ctx.reconfigure();
    }

    /**
     * Stop logs from printing to console but still saves them to log file
     */
    public static void disableConsoleLogging() {
        System.setProperty("consoleLogLevel", "OFF");
        ctx.reconfigure();
    }
}