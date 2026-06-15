package com.flightfinder.service.api;

import com.flightfinder.model.Airport;
import com.flightfinder.model.FlightItinerary;
import com.flightfinder.model.SearchCriteria;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlightDataCache {

    private final Map<String, CachedSearchResult> searchCache = new ConcurrentHashMap<>();
    private final Map<String, Airport> airportCache = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 100;
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);
    
    private final ScheduledExecutorService cleanupExecutor;

    public FlightDataCache() {
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CacheCleanup");
            t.setDaemon(true);
            return t;
        });
        
        // Run cleanup every 5 minutes
        this.cleanupExecutor.scheduleAtFixedRate(this::clearExpiredCache, 5, 5, TimeUnit.MINUTES);
    }

    public Optional<List<FlightItinerary>> getCachedSearch(SearchCriteria criteria) {
        String key = generateCacheKey(criteria);
        CachedSearchResult result = searchCache.get(key);
        
        if (result != null && !result.isExpired()) {
            return Optional.of(result.results);
        }
        return Optional.empty();
    }

    public void cacheSearchResults(SearchCriteria criteria, List<FlightItinerary> results) {
        if (searchCache.size() >= MAX_CACHE_SIZE) {
            clearExpiredCache(); // Try to make room
            if (searchCache.size() >= MAX_CACHE_SIZE) {
                // If still full, remove oldest (simple strategy)
                searchCache.keySet().stream().findFirst().ifPresent(searchCache::remove);
            }
        }
        String key = generateCacheKey(criteria);
        searchCache.put(key, new CachedSearchResult(results));
    }

    public void cacheAirport(Airport airport) {
        if (airport != null && airport.getAirportCode() != null) {
            airportCache.put(airport.getAirportCode(), airport);
        }
    }
    
    public Airport getCachedAirport(String code) {
        return airportCache.get(code);
    }

    private void clearExpiredCache() {
        searchCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private String generateCacheKey(SearchCriteria c) {
        // Simple key generation
        return String.format("%s-%s-%s-%d-%b", 
            c.getOriginAirportCode(), 
            c.getDestinationAirportCode(), 
            c.getDepartureDate(), 
            c.getNumberOfPassengers(),
            c.isDirectFlightsOnly());
    }

    private static class CachedSearchResult {
        final List<FlightItinerary> results;
        final LocalDateTime timestamp;

        CachedSearchResult(List<FlightItinerary> results) {
            this.results = results;
            this.timestamp = LocalDateTime.now();
        }

        boolean isExpired() {
            return Duration.between(timestamp, LocalDateTime.now()).compareTo(CACHE_TTL) > 0;
        }
    }
}
