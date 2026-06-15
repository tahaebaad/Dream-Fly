package com.flightfinder.service;

import com.flightfinder.model.FlightItinerary;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FlightSortService {

    private FlightSortStrategy strategy;

    public void setSortStrategy(FlightSortStrategy strategy) {
        this.strategy = strategy;
    }

    public void sortFlights(List<FlightItinerary> flights) {
        if (strategy != null) {
            strategy.sort(flights);
        }
    }


    public List<FlightItinerary> sortByPrice(List<FlightItinerary> flights, boolean ascending) {
        Comparator<FlightItinerary> comparator = Comparator.comparing(FlightItinerary::getTotalPrice);
        if (!ascending) comparator = comparator.reversed();
        
        flights.sort(comparator);
        return flights;
    }

    public List<FlightItinerary> sortByTravelTime(List<FlightItinerary> flights, boolean ascending) {
        Comparator<FlightItinerary> comparator = Comparator.comparing(FlightItinerary::getTotalTravelTime);
        if (!ascending) comparator = comparator.reversed();
        
        flights.sort(comparator);
        return flights;
    }

    public List<FlightItinerary> sortByDepartureTime(List<FlightItinerary> flights, boolean ascending) {
        Comparator<FlightItinerary> comparator = Comparator.comparing(FlightItinerary::getDepartureTime);
        if (!ascending) comparator = comparator.reversed();
        
        flights.sort(comparator);
        return flights;
    }
    
    public List<FlightItinerary> sortByArrivalTime(List<FlightItinerary> flights, boolean ascending) {
        Comparator<FlightItinerary> comparator = Comparator.comparing(FlightItinerary::getArrivalTime);
        if (!ascending) comparator = comparator.reversed();
        
        flights.sort(comparator);
        return flights;
    }

    public List<FlightItinerary> sortByNumberOfStops(List<FlightItinerary> flights, boolean ascending) {
        Comparator<FlightItinerary> comparator = Comparator.comparingInt(FlightItinerary::getNumberOfStops);
        if (!ascending) comparator = comparator.reversed();
        
        flights.sort(comparator);
        return flights;
    }
    
    public List<FlightItinerary> sortByLayoverTime(List<FlightItinerary> flights, boolean ascending) {
        Comparator<FlightItinerary> comparator = Comparator.comparing(FlightItinerary::calculateTotalLayoverTime);
        if (!ascending) comparator = comparator.reversed();
        
        flights.sort(comparator);
        return flights;
    }

    public List<FlightItinerary> sortByAirlineRating(List<FlightItinerary> flights, boolean descending) {
        // Average rating of airlines in the itinerary? Or just the first carrier?
        // Simple approach: Rating of the first operating carrier
        Comparator<FlightItinerary> comparator = Comparator.comparingDouble(f -> 
            f.getSegments().isEmpty() ? 0.0 : f.getSegments().get(0).getAirline().getAverageRating()
        );
        if (descending) comparator = comparator.reversed();
        
        flights.sort(comparator);
        return flights;
    }
}
