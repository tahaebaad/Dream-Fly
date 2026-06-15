package com.flightfinder.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;
    private static ConfigManager instance;

    private ConfigManager() {
        properties = new Properties();
        loadConfiguration();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void loadConfiguration() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Could not load configuration file: " + e.getMessage());
            // Load defaults
            properties.setProperty("amadeus.api.key", "YOUR_API_KEY");
            properties.setProperty("amadeus.api.secret", "YOUR_API_SECRET");
            properties.setProperty("currency", "USD");
        }
    }

    public void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Dream Fly Configuration");
        } catch (IOException e) {
            System.err.println("Could not save configuration: " + e.getMessage());
        }
    }

    public String getProperty(String key, String defaultValue) {
        // First check environment variables
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null) return envValue;
        
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
