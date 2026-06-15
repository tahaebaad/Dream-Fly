package com.flightfinder.util;

import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    private static final Map<String, Double> rates = new HashMap<>();

    static {
        // Mock rates relative to USD
        rates.put("USD", 1.0);
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.79);
        rates.put("PKR", 280.0);
        rates.put("JPY", 148.0);
        rates.put("AED", 3.67);
    }

    public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) return amount;
        
        double fromRate = rates.getOrDefault(fromCurrency, 1.0);
        double toRate = rates.getOrDefault(toCurrency, 1.0);
        
        // Convert to USD first, then to target
        double amountInUSD = amount / fromRate;
        return amountInUSD * toRate;
    }

    public double getExchangeRate(String from, String to) {
        return convertCurrency(1.0, from, to);
    }

    public void fetchLatestRates() {
        // In a real app, this would call an API
        Logger.logInfo("Fetching latest currency rates (Mock)...");
    }

    public String getCurrencySymbol(String currencyCode) {
        switch (currencyCode) {
            case "USD": return "$";
            case "EUR": return "€";
            case "GBP": return "£";
            case "PKR": return "Rs";
            case "JPY": return "¥";
            default: return currencyCode;
        }
    }
}
