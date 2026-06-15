package com.flightfinder.service;

import com.flightfinder.model.FlightItinerary;
import java.util.Comparator;
import java.util.List;

public class PriceSortStrategy implements FlightSortStrategy {
    @Override
    public void sort(List<FlightItinerary> flights) {
        flights.sort(Comparator.comparing(FlightItinerary::getTotalPrice));
    }
}
