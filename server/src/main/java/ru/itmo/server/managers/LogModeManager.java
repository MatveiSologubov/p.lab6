package ru.itmo.server.managers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class LogModeManager {
    private static final LoggerContext ctx =
            (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
    private static final Configuration config = ctx.getConfiguration();
    private static final LoggerConfig loggerConfig =
            config.getLoggerConfig("ru.itmo.server");

    public static void enableConsoleLogging() {
        loggerConfig.setLevel(Level.ALL);
        ctx.updateLoggers(config);
    }

    public static void disableConsoleLogging() {
        loggerConfig.setLevel(Level.OFF);
        ctx.updateLoggers(config);
    }
}