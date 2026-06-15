package com.flightfinder.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.io.Serializable;

public class Layover implements Serializable {
    private static final long serialVersionUID = 1L;
    private Airport layoverAirport;
    private Duration layoverDuration;
    private LocalDateTime arrivalAtLayover;
    private LocalDateTime departureFromLayover;
    private String terminal;
    private boolean requiresVisaTransit;

    public Layover() {}

    public Layover(Airport layoverAirport, LocalDateTime arrival, LocalDateTime departure) {
        this.layoverAirport = layoverAirport;
        this.arrivalAtLayover = arrival;
        this.departureFromLayover = departure;
        this.layoverDuration = Duration.between(arrival, departure);
    }

    public boolean isLongLayover() {
        return layoverDuration != null && layoverDuration.toHours() > 4;
    }

    public long getLayoverHours() {
        return layoverDuration != null ? layoverDuration.toHours() : 0;
    }

    // Getters and Setters
    public Airport getLayoverAirport() { return layoverAirport; }
    public void setLayoverAirport(Airport layoverAirport) { this.layoverAirport = layoverAirport; }

    public Duration getLayoverDuration() { return layoverDuration; }
    public void setLayoverDuration(Duration layoverDuration) { this.layoverDuration = layoverDuration; }

    public LocalDateTime getArrivalAtLayover() { return arrivalAtLayover; }
    public void setArrivalAtLayover(LocalDateTime arrivalAtLayover) { this.arrivalAtLayover = arrivalAtLayover; }

    public LocalDateTime getDepartureFromLayover() { return departureFromLayover; }
    public void setDepartureFromLayover(LocalDateTime departureFromLayover) { this.departureFromLayover = departureFromLayover; }

    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    public boolean isRequiresVisaTransit() { return requiresVisaTransit; }
    public void setRequiresVisaTransit(boolean requiresVisaTransit) { this.requiresVisaTransit = requiresVisaTransit; }
}
