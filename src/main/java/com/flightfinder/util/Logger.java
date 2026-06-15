package com.flightfinder.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {
    
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("FlightFinder");
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;
        try {
            FileHandler fh = new FileHandler("application.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logInfo(String message) {
        init();
        logger.info(message);
    }

    public static void logError(String message, Exception e) {
        init();
        logger.log(Level.SEVERE, message, e);
    }

    public static void logDebug(String message) {
        init();
        logger.fine(message);
    }
}
