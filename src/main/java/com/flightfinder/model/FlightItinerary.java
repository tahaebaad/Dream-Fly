package com.flightfinder.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.Serializable;

public class FlightItinerary implements Comparable<FlightItinerary>, Serializable {
    private static final long serialVersionUID = 1L;
    private String itineraryId;
    private List<FlightSegment> segments = new ArrayList<>();
    private List<Layover> layovers = new ArrayList<>();
    private double totalPrice;
    private String currency;
    private Duration totalTravelTime;
    private int numberOfStops;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private boolean isDirectFlight;
    private String bookingUrl;
    private String cabinClass;

    public void calculateTotalDuration() {
        if (segments == null || segments.isEmpty()) return;
        
        LocalDateTime start = segments.get(0).getDepartureTime();
        LocalDateTime end = segments.get(segments.size() - 1).getArrivalTime();
        
        if (start != null && end != null) {
            this.totalTravelTime = Duration.between(start, end);
        }
    }

    public Duration calculateTotalLayoverTime() {
        return layovers.stream()
                .map(Layover::getLayoverDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public int getNumberOfStops() {
        return layovers.size();
    }

    public boolean isDirectFlight() {
        return getNumberOfStops() == 0;
    }

    public String getSegmentsSummary() {
        if (segments.isEmpty()) return "";
        String origin = segments.get(0).getDepartureAirport().getAirportCode();
        String destination = segments.get(segments.size() - 1).getArrivalAirport().getAirportCode();
        
        List<String> route = new ArrayList<>();
        route.add(origin);
        for (Layover l : layovers) {
            route.add(l.getLayoverAirport().getAirportCode());
        }
        route.add(destination);
        
        return String.join(" -> ", route);
    }

    @Override
    public int compareTo(FlightItinerary o) {
        return Double.compare(this.totalPrice, o.totalPrice);
    }

    // Getters and Setters
    public String getItineraryId() { return itineraryId; }
    public void setItineraryId(String itineraryId) { this.itineraryId = itineraryId; }

    public List<FlightSegment> getSegments() { return segments; }
    public void setSegments(List<FlightSegment> segments) { this.segments = segments; }

    public List<Layover> getLayovers() { return layovers; }
    public void setLayovers(List<Layover> layovers) { this.layovers = layovers; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Duration getTotalTravelTime() { return totalTravelTime; }
    public void setTotalTravelTime(Duration totalTravelTime) { this.totalTravelTime = totalTravelTime; }

    public void setNumberOfStops(int numberOfStops) { this.numberOfStops = numberOfStops; }
    
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public void setDirectFlight(boolean directFlight) { isDirectFlight = directFlight; }

    public String getBookingUrl() { return bookingUrl; }
    public void setBookingUrl(String bookingUrl) { this.bookingUrl = bookingUrl; }

    public String getCabinClass() { return cabinClass; }
    public void setCabinClass(String cabinClass) { this.cabinClass = cabinClass; }
}
