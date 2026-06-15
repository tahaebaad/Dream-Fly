package com.flightfinder.ui;

import com.flightfinder.model.Booking;
import com.flightfinder.model.FlightItinerary;
import com.flightfinder.util.DateTimeUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Factory pattern for creating complex UI components.
 * (GoF Creational Pattern: Factory)
 */
public class UIComponentFactory {

    public static FlightCard createFlightCard(FlightItinerary itinerary) {
        return new FlightCard(itinerary);
    }

    public static VBox createBookingCard(Booking booking, java.util.function.Consumer<String> onCancel) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #F0F4F8; -fx-padding: 16; "
                + "-fx-background-radius: 10px; -fx-border-color: #CBD5E1; "
                + "-fx-border-radius: 10px; -fx-border-width: 1px;");

        HBox header = new HBox();
        Label idLabel = new Label("PNR: " + booking.getBookingId());
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #3772FF;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label priceLabel = new Label(String.format("$%.0f", booking.getItinerary().getTotalPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #1A1A1A;");
        header.getChildren().addAll(idLabel, spacer, priceLabel);

        Label passengerLabel = new Label("Passenger: " + booking.getPassengerName());
        passengerLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1A1A1A; -fx-font-weight: 600;");

        String routeText = "Unknown Route";
        String dateText = "";
        if (!booking.getItinerary().getSegments().isEmpty()) {
            var first = booking.getItinerary().getSegments().get(0);
            var last = booking.getItinerary().getSegments().get(booking.getItinerary().getSegments().size() - 1);
            routeText = first.getDepartureAirport().getAirportCode() + " → " + last.getArrivalAirport().getAirportCode();
            dateText = DateTimeUtils.formatDateTime(first.getDepartureTime(), "MMM dd, yyyy · HH:mm");
        }

        Label routeLabel = new Label(routeText);
        routeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1A1A1A;");

        Label dateLabel = new Label(dateText);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7A99;");
        
        Button cancelBtn = new Button("CANCEL BOOKING");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E63946; -fx-border-color: #E63946; -fx-border-radius: 6px; -fx-padding: 4 10 4 10; -fx-font-size: 11px; -fx-font-weight: 700; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> onCancel.accept(booking.getBookingId()));
        
        HBox footer = new HBox();
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        footer.getChildren().addAll(dateLabel, footerSpacer, cancelBtn);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(header, passengerLabel, routeLabel, footer);
        return card;
    }
}
