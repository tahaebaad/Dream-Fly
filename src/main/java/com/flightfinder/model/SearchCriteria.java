package com.flightfinder.model;

import java.time.LocalDate;
import java.util.Set;

public class SearchCriteria {
    private String originAirportCode;
    private String destinationAirportCode;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private int numberOfPassengers;
    private String cabinClass;
    private boolean directFlightsOnly;
    private Set<String> preferredAirlines;
    private double maxPrice;
    private int maxLayovers;

    public SearchCriteria() {}

    // Getters and Setters
    public String getOriginAirportCode() { return originAirportCode; }
    public void setOriginAirportCode(String originAirportCode) { this.originAirportCode = originAirportCode; }

    public String getDestinationAirportCode() { return destinationAirportCode; }
    public void setDestinationAirportCode(String destinationAirportCode) { this.destinationAirportCode = destinationAirportCode; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public int getNumberOfPassengers() { return numberOfPassengers; }
    public void setNumberOfPassengers(int numberOfPassengers) { this.numberOfPassengers = numberOfPassengers; }

    public String getCabinClass() { return cabinClass; }
    public void setCabinClass(String cabinClass) { this.cabinClass = cabinClass; }

    public boolean isDirectFlightsOnly() { return directFlightsOnly; }
    public void setDirectFlightsOnly(boolean directFlightsOnly) { this.directFlightsOnly = directFlightsOnly; }

    public Set<String> getPreferredAirlines() { return preferredAirlines; }
    public void setPreferredAirlines(Set<String> preferredAirlines) { this.preferredAirlines = preferredAirlines; }

    public double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }

    public int getMaxLayovers() { return maxLayovers; }
    public void setMaxLayovers(int maxLayovers) { this.maxLayovers = maxLayovers; }
}
