package com.flightfinder.service;

import com.flightfinder.model.FlightItinerary;
import com.flightfinder.model.SearchCriteria;
import com.flightfinder.model.FlightSegment;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FlightFilterService {

    public List<FlightItinerary> filterByPrice(List<FlightItinerary> flights, double minPrice, double maxPrice) {
        return flights.stream()
                .filter(f -> f.getTotalPrice() >= minPrice && f.getTotalPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<FlightItinerary> filterByNumberOfStops(List<FlightItinerary> flights, int maxStops) {
        return flights.stream()
                .filter(f -> f.getNumberOfStops() <= maxStops)
                .collect(Collectors.toList());
    }

    public List<FlightItinerary> filterByAirlines(List<FlightItinerary> flights, Set<String> airlineCodes) {
        if (airlineCodes == null || airlineCodes.isEmpty()) return flights;
        
        return flights.stream()
                .filter(f -> f.getSegments().stream()
                        .anyMatch(seg -> airlineCodes.contains(seg.getAirline().getAirlineCode())))
                .collect(Collectors.toList());
    }

    public List<FlightItinerary> filterByDepartureTime(List<FlightItinerary> flights, LocalTime earliest, LocalTime latest) {
        return flights.stream()
                .filter(f -> {
                    LocalTime depTime = f.getDepartureTime().toLocalTime();
                    return !depTime.isBefore(earliest) && !depTime.isAfter(latest);
                })
                .collect(Collectors.toList());
    }
    
    public List<FlightItinerary> filterByArrivalTime(List<FlightItinerary> flights, LocalTime earliest, LocalTime latest) {
        return flights.stream()
                .filter(f -> {
                    LocalTime arrTime = f.getArrivalTime().toLocalTime();
                    return !arrTime.isBefore(earliest) && !arrTime.isAfter(latest);
                })
                .collect(Collectors.toList());
    }

    public List<FlightItinerary> filterByLayoverDuration(List<FlightItinerary> flights, Duration minLayover, Duration maxLayover) {
        return flights.stream()
                .filter(f -> {
                    if (f.isDirectFlight()) return true;
                    // Check if ALL layovers are within range? Or ANY? Usually we filter OUT if any layover is bad.
                    // Let's say we filter out if ANY layover is outside the range.
                    return f.getLayovers().stream()
                            .allMatch(l -> {
                                Duration d = l.getLayoverDuration();
                                return d.compareTo(minLayover) >= 0 && d.compareTo(maxLayover) <= 0;
                            });
                })
                .collect(Collectors.toList());
    }

    public List<FlightItinerary> filterByTravelTime(List<FlightItinerary> flights, Duration maxTotalTime) {
        return flights.stream()
                .filter(f -> f.getTotalTravelTime().compareTo(maxTotalTime) <= 0)
                .collect(Collectors.toList());
    }
}
