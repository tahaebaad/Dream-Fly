package com.flightfinder.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bookingId;
    private String passengerName;
    private FlightItinerary itinerary;
    private LocalDateTime bookingDate;

    public Booking() {}

    public Booking(String passengerName, FlightItinerary itinerary) {
        this.bookingId = generateBookingId();
        this.passengerName = passengerName;
        this.itinerary = itinerary;
        this.bookingDate = LocalDateTime.now();
    }

    private String generateBookingId() {
        // Generate a simple alphanumeric booking ID (e.g. "DF-8A9B")
        return "DF-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public FlightItinerary getItinerary() { return itinerary; }
    public void setItinerary(FlightItinerary itinerary) { this.itinerary = itinerary; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
