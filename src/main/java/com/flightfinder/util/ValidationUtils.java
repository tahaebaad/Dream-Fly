package com.flightfinder.util;

import java.time.LocalDate;

public class ValidationUtils {

    public static boolean isValidAirportCode(String code) {
        return code != null && code.length() == 3 && code.matches("[A-Z]{3}");
    }

    public static boolean isValidPrice(double price) {
        return price >= 0;
    }

    public static boolean isValidDateRange(LocalDate departure, LocalDate returnDate) {
        if (departure == null) return false;
        if (departure.isBefore(LocalDate.now())) return false; // Can't fly in past
        if (returnDate != null && returnDate.isBefore(departure)) return false;
        return true;
    }

    public static boolean isValidPassengerCount(int count) {
        return count > 0 && count <= 9;
    }
}
