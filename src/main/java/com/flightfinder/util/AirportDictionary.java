package com.flightfinder.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class AirportDictionary {
    
    public static Map<String, String> getMajorAirports() {
        Map<String, String> airports = new LinkedHashMap<>();
        
        // North America
        airports.put("JFK", "New York, USA");
        airports.put("LAX", "Los Angeles, USA");
        airports.put("IAD", "Washington DC, USA");
        airports.put("YYZ", "Toronto, Canada");
        airports.put("MEX", "Mexico City, Mexico");
        
        // Europe
        airports.put("LHR", "London, UK");
        airports.put("CDG", "Paris, France");
        airports.put("FRA", "Frankfurt, Germany");
        airports.put("AMS", "Amsterdam, Netherlands");
        airports.put("MAD", "Madrid, Spain");
        airports.put("FCO", "Rome, Italy");
        airports.put("ZRH", "Zurich, Switzerland");
        airports.put("IST", "Istanbul, Turkey");
        
        // Middle East
        airports.put("DXB", "Dubai, UAE");
        airports.put("AUH", "Abu Dhabi, UAE");
        airports.put("DOH", "Doha, Qatar");
        airports.put("RUH", "Riyadh, Saudi Arabia");
        
        // Asia
        airports.put("HND", "Tokyo, Japan");
        airports.put("PEK", "Beijing, China");
        airports.put("HKG", "Hong Kong");
        airports.put("SIN", "Singapore");
        airports.put("BKK", "Bangkok, Thailand");
        airports.put("DEL", "New Delhi, India");
        airports.put("BOM", "Mumbai, India");
        airports.put("ISB", "Islamabad, Pakistan");
        airports.put("LHE", "Lahore, Pakistan");
        airports.put("KHI", "Karachi, Pakistan");
        
        // Oceania
        airports.put("SYD", "Sydney, Australia");
        airports.put("AKL", "Auckland, New Zealand");
        
        // South America
        airports.put("GRU", "Sao Paulo, Brazil");
        airports.put("EZE", "Buenos Aires, Argentina");
        
        // Africa
        airports.put("JNB", "Johannesburg, South Africa");
        airports.put("CAI", "Cairo, Egypt");
        
        return airports;
    }
}
