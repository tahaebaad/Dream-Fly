package com.flightfinder.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    
    public static final String API_DATE_FORMAT = "yyyy-MM-dd";
    public static final String API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
    public static final String DISPLAY_TIME_FORMAT = "HH:mm";

    public static String formatDateTime(LocalDateTime dt, String pattern) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatDuration(Duration duration) {
        if (duration == null) return "";
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%dh %dm", hours, minutes);
    }

    public static int calculateDayDifference(LocalDate from, LocalDate to) {
        if (from == null || to == null) return 0;
        return (int) ChronoUnit.DAYS.between(from, to);
    }

    public static boolean isOvernight(LocalDateTime departure, LocalDateTime arrival) {
        if (departure == null || arrival == null) return false;
        return !departure.toLocalDate().equals(arrival.toLocalDate());
    }

    public static LocalDateTime parseAPIDateTime(String apiDateString) {
        if (apiDateString == null || apiDateString.isEmpty()) return null;
        // Amadeus often returns "2023-11-01T10:30:00"
        return LocalDateTime.parse(apiDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static LocalDateTime convertTimezone(LocalDateTime dt, String fromZone, String toZone) {
        if (dt == null) return null;
        ZoneId fromId = ZoneId.of(fromZone);
        ZoneId toId = ZoneId.of(toZone);
        ZonedDateTime zdt = ZonedDateTime.of(dt, fromId);
        return zdt.withZoneSameInstant(toId).toLocalDateTime();
    }
}
