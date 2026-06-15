package com.flightfinder.service;

import com.flightfinder.model.FlightItinerary;
import java.util.Comparator;
import java.util.List;

public class StopsSortStrategy implements FlightSortStrategy {
    @Override
    public void sort(List<FlightItinerary> flights) {
        flights.sort(Comparator.comparingInt(FlightItinerary::getNumberOfStops));
    }
}
