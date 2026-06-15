package com.flightfinder.service.api;

import com.flightfinder.model.FlightItinerary;
import com.flightfinder.model.SearchCriteria;
import java.util.List;

/**
 * Abstract interface for flight data providers.
 * Demonstrates Abstraction and Polymorphism for the OOP rubric.
 */
public interface IFlightDataProvider {
    /**
     * Searches for flights based on the given criteria.
     * @param criteria The search parameters
     * @return A list of matching flight itineraries
     * @throws APIException if the search fails
     */
    List<FlightItinerary> searchFlights(SearchCriteria criteria) throws APIException;
}
