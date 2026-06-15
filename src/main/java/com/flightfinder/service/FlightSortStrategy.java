package com.flightfinder.service;

import com.flightfinder.model.FlightItinerary;
import java.util.List;

/**
 * Strategy interface for sorting flight itineraries.
 * (GoF Behavioral Pattern: Strategy)
 */
public interface FlightSortStrategy {
    void sort(List<FlightItinerary> flights);
}
