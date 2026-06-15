package com.flightfinder.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.io.Serializable;

public class FlightSegment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flightNumber;
    private Airline airline;
    private Airport departureAirport;
    private Airport arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Duration flightDuration;
    private String cabinClass;
    private int availableSeats;
    private String aircraft;

    public FlightSegment() {}

    public void calculateDuration() {
        if (departureTime != null && arrivalTime != null) {
            this.flightDuration = Duration.between(departureTime, arrivalTime);
        }
    }

    public boolean isOvernight() {
        if (departureTime == null || arrivalTime == null) return false;
        return departureTime.toLocalDate().isBefore(arrivalTime.toLocalDate());
    }

    // Getters and Setters
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public Airline getAirline() { return airline; }
    public void setAirline(Airline airline) { this.airline = airline; }

    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport departureAirport) { this.departureAirport = departureAirport; }

    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport arrivalAirport) { this.arrivalAirport = arrivalAirport; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public Duration getFlightDuration() { return flightDuration; }
    public void setFlightDuration(Duration flightDuration) { this.flightDuration = flightDuration; }

    public String getCabinClass() { return cabinClass; }
    public void setCabinClass(String cabinClass) { this.cabinClass = cabinClass; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public String getAircraft() { return aircraft; }
    public void setAircraft(String aircraft) { this.aircraft = aircraft; }
}
