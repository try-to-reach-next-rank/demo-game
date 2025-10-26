package com.example.demo.model.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple logging utility for SLF4J.
 * Provides static methods like info(), debug(), warn(), error()
 * that automatically use the calling class as logger.
 */
public class Logging {

    private static final Map<String, Logger> cache = new ConcurrentHashMap<>();

    // Private constructor to prevent instantiation
    private Logging() {}

    /**
     * Get a logger for the calling class.
     * Uses stack trace to find the class that called the logging method.
     */
    private static Logger getLogger() {
        // // new Exception() is used to get current stack trace
        // String className = new Exception().getStackTrace()[0].getClassName();
        // return cache.computeIfAbsent(className, LoggerFactory::getLogger);
        StackTraceElement[] stack = new Exception().getStackTrace();
        for (StackTraceElement e : stack) {
            if (!e.getClassName().equals(Logging.class.getName())) {
                return cache.computeIfAbsent(e.getClassName(), LoggerFactory::getLogger);
            }
        }
        // fallback
        return LoggerFactory.getLogger(Logging.class);
    }

    // --------- Convenience logging methods ---------

    public static void info(String message, Object... args) {
        getLogger().info(message, args);
    }

    public static void debug(String message, Object... args) {
        getLogger().debug(message, args);
    }

    public static void warn(String message, Object... args) {
        getLogger().warn(message, args);
    }

    public static void error(String message, Object... args) {
        getLogger().error(message, args);
    }

    /**
     * Log an error with exception.
     */
    public static void error(String message, Throwable t) {
        getLogger().error(message, t);
    }
}
