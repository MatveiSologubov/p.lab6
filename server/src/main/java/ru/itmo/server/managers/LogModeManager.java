package ru.itmo.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class LogModeManager {
    private static final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

    public static void enableConsoleLogging() {
        System.setProperty("consoleLogLevel", "ALL");
        ctx.reconfigure();
    }

    public static void disableConsoleLogging() {
        System.setProperty("consoleLogLevel", "OFF");
        ctx.reconfigure();
    }
}