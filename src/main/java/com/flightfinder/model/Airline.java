package com.flightfinder.model;

import java.io.Serializable;

public class Airline implements Serializable {
    private static final long serialVersionUID = 1L;
    private String airlineCode;
    private String airlineName;
    private String logo;
    private double averageRating;

    public Airline() {}
    
    public Airline(String airlineCode, String airlineName, String logo, double averageRating) {
        this.airlineCode = airlineCode;
        this.airlineName = airlineName;
        this.logo = logo;
        this.averageRating = averageRating;
    }

    public String getAirlineCode() { return airlineCode; }
    public void setAirlineCode(String airlineCode) { this.airlineCode = airlineCode; }

    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    @Override
    public String toString() {
        return airlineName;
    }
}
