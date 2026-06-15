package com.flightfinder.service;

import com.flightfinder.db.BookingDAO;
import com.flightfinder.model.Booking;
import com.flightfinder.model.FlightItinerary;

import java.util.List;

public class BookingService {
    private final BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    /**
     * Attempts to book a flight for the given passenger.
     * @param passengerName The name of the passenger
     * @param itinerary The selected flight itinerary
     * @return The confirmed Booking object, or null if it failed.
     * @throws IllegalArgumentException if passenger name is invalid
     */
    public Booking bookFlight(String passengerName, FlightItinerary itinerary) {
        if (passengerName == null || passengerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty.");
        }
        if (itinerary == null) {
            throw new IllegalArgumentException("Flight itinerary cannot be null.");
        }

        Booking booking = new Booking(passengerName.trim(), itinerary);
        boolean success = bookingDAO.saveBooking(booking);
        
        if (success) {
            return booking;
        }
        return null;
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    public boolean cancelBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return false;
        }
        return bookingDAO.deleteBooking(bookingId);
    }
}
