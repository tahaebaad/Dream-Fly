package com.flightfinder.service;

import com.flightfinder.model.FlightItinerary;
import com.flightfinder.model.SearchCriteria;
import com.flightfinder.service.api.APIException;
import com.flightfinder.service.api.IFlightDataProvider;
import com.flightfinder.service.api.FlightDataCache;
import com.flightfinder.util.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FlightSearchService {

    private final IFlightDataProvider dataProvider;
    private final FlightDataCache cache;
    private final FlightFilterService filterService;
    private final FlightSortService sortService;
    private final ExecutorService executorService;

    public FlightSearchService(IFlightDataProvider dataProvider, FlightDataCache cache) {
        this.dataProvider = dataProvider;
        this.cache = cache;
        this.filterService = new FlightFilterService();
        this.sortService = new FlightSortService();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public List<FlightItinerary> searchFlights(SearchCriteria criteria) throws APIException {
        // 1. Check cache
        Optional<List<FlightItinerary>> cachedResults = cache.getCachedSearch(criteria);
        if (cachedResults.isPresent()) {
            Logger.logInfo("Returning cached search results");
            return cachedResults.get();
        }

        // 2. Call API provider
        Logger.logInfo("Cache miss. Calling Data Provider...");
        List<FlightItinerary> results = dataProvider.searchFlights(criteria);
        
        // Ensure cabin class is tagged correctly
        for (FlightItinerary f : results) {
             if (criteria.getCabinClass() != null) {
                 f.setCabinClass(criteria.getCabinClass());
             }
        }
        
        // 3. Cache results
        cache.cacheSearchResults(criteria, results);
        
        return results;
    }

    public CompletableFuture<List<FlightItinerary>> searchFlightsAsync(SearchCriteria criteria) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return searchFlights(criteria);
            } catch (APIException e) {
                Logger.logError("Async search failed", e);
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
    
    public FlightFilterService getFilterService() { return filterService; }
    public FlightSortService getSortService() { return sortService; }
}
