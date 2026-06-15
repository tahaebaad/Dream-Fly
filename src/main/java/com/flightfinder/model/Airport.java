package com.flightfinder.model;

import java.util.Objects;
import java.io.Serializable;

public class Airport implements Serializable {
    private static final long serialVersionUID = 1L;
    private String airportCode;
    private String airportName;
    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private String timezone;

    public Airport() {}

    public Airport(String airportCode, String airportName, String city, String country, double latitude, double longitude, String timezone) {
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
    }

    public String getAirportCode() { return airportCode; }
    public void setAirportCode(String airportCode) { this.airportCode = airportCode; }

    public String getAirportName() { return airportName; }
    public void setAirportName(String airportName) { this.airportName = airportName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", airportCode, airportName, city);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport) o;
        return Objects.equals(airportCode, airport.airportCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airportCode);
    }
}
