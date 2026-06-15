package com.flightfinder.db;

import com.flightfinder.model.Booking;
import com.flightfinder.util.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private static final String DATA_FILE = "bookings.ser";

    @SuppressWarnings("unchecked")
    public List<Booking> getAllBookings() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Booking>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Logger.logError("Failed to read bookings from file", e);
            return new ArrayList<>();
        }
    }

    public boolean saveBooking(Booking newBooking) {
        List<Booking> currentBookings = getAllBookings();
        currentBookings.add(newBooking);
        return saveAll(currentBookings);
    }

    private boolean saveAll(List<Booking> bookings) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(bookings);
            return true;
        } catch (IOException e) {
            Logger.logError("Failed to write bookings to file", e);
            return false;
        }
    }
    public boolean deleteBooking(String bookingId) {
        List<Booking> currentBookings = getAllBookings();
        boolean removed = currentBookings.removeIf(b -> b.getBookingId().equals(bookingId));
        if (removed) {
            return saveAll(currentBookings);
        }
        return false;
    }
}
