package com.flightfinder.ui;

import com.flightfinder.model.FlightItinerary;
import com.flightfinder.util.DateTimeUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class FlightCard extends VBox {

    @FXML private ImageView airlineLogo;
    @FXML private Label airlineCode;
    @FXML private Label airlineName;
    @FXML private Label aircraft;          // badge: flight number
    @FXML private Label aircraftType;      // under name: aircraft model
    @FXML private Label departureTime;
    @FXML private Label departureAirport;
    @FXML private Label departureCity;
    @FXML private Label duration;
    @FXML private Label stops;
    @FXML private Label arrivalTime;
    @FXML private Label arrivalAirport;
    @FXML private Label arrivalCity;
    @FXML private Label price;
    @FXML private Label priceMeta;
    @FXML private Button viewButton;

    public FlightCard(FlightItinerary flight) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/flightfinder/ui/FlightCard.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FlightCard", e);
        }

        populate(flight);
    }

    private void populate(FlightItinerary flight) {
        if (flight.getSegments().isEmpty()) return;

        var firstSeg = flight.getSegments().get(0);
        var lastSeg  = flight.getSegments().get(flight.getSegments().size() - 1);

        // ── Airline badge (IATA code + flight number) ──
        String iataCode = firstSeg.getAirline().getAirlineCode();
        if (iataCode == null || iataCode.isBlank()) iataCode = "??";
        airlineCode.setText(iataCode);
        aircraft.setText(iataCode + " · " + firstSeg.getFlightNumber());

        // ── Airline logo & name ──
        String name = firstSeg.getAirline().getAirlineName();
        if (name != null) {
            if (name.length() > 15) {
                name = name.replace("Airlines", "").replace("Airways", "").replace("Royal Dutch", "").trim();
                if (name.length() > 15) {
                    name = name.substring(0, 14).trim() + "…";
                }
            }
            airlineName.setText(name);
        } else {
            airlineName.setText("");
        }
        aircraftType.setText(firstSeg.getAircraft() != null ? firstSeg.getAircraft() : "");

        try {
            String logoUrl = firstSeg.getAirline().getLogo();
            if (logoUrl != null && !logoUrl.isEmpty()) {
                airlineLogo.setImage(new Image(logoUrl, true));
            }
        } catch (Exception e) {
            // Ignore logo load errors silently
        }

        // ── Times ──
        departureTime.setText(DateTimeUtils.formatDateTime(flight.getDepartureTime(), "HH:mm"));
        departureAirport.setText(firstSeg.getDepartureAirport().getAirportCode());
        departureCity.setText(firstSeg.getDepartureAirport().getCity() != null
                ? firstSeg.getDepartureAirport().getCity() : "");

        arrivalTime.setText(DateTimeUtils.formatDateTime(flight.getArrivalTime(), "HH:mm"));
        arrivalAirport.setText(lastSeg.getArrivalAirport().getAirportCode());
        arrivalCity.setText(lastSeg.getArrivalAirport().getCity() != null
                ? lastSeg.getArrivalAirport().getCity() : "");

        // ── Journey ──
        duration.setText(DateTimeUtils.formatDuration(flight.getTotalTravelTime()));

        int numStops = flight.getNumberOfStops();
        if (numStops == 0) {
            stops.setText("● DIRECT");
            stops.getStyleClass().removeAll("stops-warning");
            stops.getStyleClass().add("stops-direct");
        } else {
            String layoverInfo = buildLayoverInfo(flight, numStops);
            stops.setText("● " + numStops + " STOP" + (numStops > 1 ? "S" : "") + layoverInfo);
            stops.getStyleClass().removeAll("stops-direct");
            stops.getStyleClass().add("stops-warning");
        }

        // ── Price ──
        price.setText(String.format("$%.0f", flight.getTotalPrice()));
        
        String cClass = flight.getCabinClass() != null ? flight.getCabinClass().replace("_", " ") : "ECONOMY";
        priceMeta.setText("PER PERSON · " + cClass.toUpperCase());

        // ── Action ──
        viewButton.setOnAction(e -> new FlightDetailDialog(flight).showAndWait());
    }

    private String buildLayoverInfo(FlightItinerary flight, int numStops) {
        if (flight.getLayovers() == null || flight.getLayovers().isEmpty()) return "";
        try {
            var layover = flight.getLayovers().get(0);
            long mins = layover.getLayoverDuration() != null
                    ? layover.getLayoverDuration().toMinutes() : 0;
            long h = mins / 60, m = mins % 60;
            String airport = layover.getLayoverAirport() != null
                    ? layover.getLayoverAirport().getAirportCode() : "";
            return String.format(" · %dh %02dm in %s", h, m, airport);
        } catch (Exception e) {
            return "";
        }
    }
}
