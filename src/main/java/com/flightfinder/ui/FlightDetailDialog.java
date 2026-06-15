package com.flightfinder.ui;

import com.flightfinder.model.FlightItinerary;
import com.flightfinder.model.FlightSegment;
import com.flightfinder.model.Layover;
import com.flightfinder.util.DateTimeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.ActionEvent;
import java.util.Optional;
import com.flightfinder.service.BookingService;
import com.flightfinder.model.Booking;

public class FlightDetailDialog extends Dialog<Void> {

    private static final String MAIN_BG      = "#FFFFFF";
    private static final String PANEL_BG     = "#F0F4F8";
    private static final String BORDER_COLOR = "#CBD5E1";
    private static final String TEXT_PRIMARY  = "#1A1A1A";
    private static final String TEXT_MUTED    = "#6B7A99";
    private static final String TEXT_ACCENT   = "#6B7A99";
    private static final String ACCENT_BLUE   = "#3772FF";
    private static final String ACCENT_GREEN  = "#05CD99";
    private static final String ACCENT_ORANGE = "#FFB547";

    public FlightDetailDialog(FlightItinerary itinerary) {
        setTitle("Dream Fly · Flight Details");
        setHeaderText(null);

        // ── Dialog pane styling ──
        getDialogPane().setStyle(
            "-fx-background-color: " + MAIN_BG + ";"
        );

        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + MAIN_BG + ";");
        content.setPrefWidth(640);

        // ── Header: Airline + Price ──
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + PANEL_BG + "; -fx-background-radius: 12px; "
                + "-fx-padding: 16; -fx-border-color: " + BORDER_COLOR + "; "
                + "-fx-border-radius: 12px; -fx-border-width: 1px;");

        if (!itinerary.getSegments().isEmpty()) {
            var seg0 = itinerary.getSegments().get(0);
            try {
                ImageView logo = new ImageView(seg0.getAirline().getLogo());
                logo.setFitWidth(48);
                logo.setFitHeight(48);
                logo.setPreserveRatio(true);
                header.getChildren().add(logo);
            } catch (Exception ignored) {}

            VBox airlineInfo = new VBox(3);
            Label airlineName = new Label(seg0.getAirline().getAirlineName());
            airlineName.setStyle("-fx-font-size: 17px; -fx-font-weight: 800; -fx-text-fill: " + TEXT_PRIMARY + ";");
            Label flightNum = new Label("Flight " + seg0.getFlightNumber()
                    + (seg0.getAircraft() != null ? "  ·  " + seg0.getAircraft() : ""));
            flightNum.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_ACCENT + ";");
            airlineInfo.getChildren().addAll(airlineName, flightNum);
            header.getChildren().add(airlineInfo);
        }

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        String cClass = itinerary.getCabinClass() != null ? itinerary.getCabinClass().replace("_", " ") : "ECONOMY";
        Label perPerson = new Label("PER PERSON · " + cClass.toUpperCase());
        perPerson.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: 600;");
        Label price = new Label(String.format("$%.0f", itinerary.getTotalPrice()));
        price.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: " + TEXT_PRIMARY + ";");
        priceBox.getChildren().addAll(perPerson, price);

        header.getChildren().addAll(headerSpacer, priceBox);

        // ── Journey summary row ──
        HBox journeySummary = buildJourneySummary(itinerary);

        // ── Segments scroll pane ──
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; "
                + "-fx-border-color: transparent;");
        scrollPane.setPrefHeight(300);

        VBox segmentsBox = new VBox(12);
        segmentsBox.setPadding(new Insets(4, 0, 4, 0));
        segmentsBox.setStyle("-fx-background-color: transparent;");

        for (int i = 0; i < itinerary.getSegments().size(); i++) {
            segmentsBox.getChildren().add(createSegmentView(itinerary.getSegments().get(i)));
            if (i < itinerary.getLayovers().size()) {
                segmentsBox.getChildren().add(createLayoverView(itinerary.getLayovers().get(i)));
            }
        }
        scrollPane.setContent(segmentsBox);

        // ── Extras row (baggage, flexibility, seat, meals) ──
        HBox extrasRow = new HBox(12);
        extrasRow.setStyle("-fx-background-color: " + PANEL_BG + "; -fx-background-radius: 10px; "
                + "-fx-padding: 14; -fx-border-color: " + BORDER_COLOR + "; "
                + "-fx-border-radius: 10px; -fx-border-width: 1px;");
        extrasRow.getChildren().addAll(
            extraCard("BAGGAGE",     "30kg + Cabin",       TEXT_ACCENT),
            extraCard("FLEXIBILITY", "Change fee $50",    ACCENT_ORANGE),
            extraCard("SEAT",        "Economy Class",     TEXT_ACCENT),
            extraCard("MEALS",       "Award-winning",     ACCENT_GREEN)
        );

        content.getChildren().addAll(header, journeySummary, scrollPane, extrasRow);

        getDialogPane().setContent(content);
        ButtonType bookType = new ButtonType("Book Now", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(bookType, ButtonType.CLOSE);

        // Style the close button
        getDialogPane().lookupButton(ButtonType.CLOSE).setStyle(
            "-fx-background-color: #FFFFFF; -fx-text-fill: #1A1A1A; "
            + "-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; "
            + "-fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 700;"
        );

        // Style and setup the Book Now button
        Button bookBtn = (Button) getDialogPane().lookupButton(bookType);
        bookBtn.setStyle(
            "-fx-background-color: #3772FF; -fx-text-fill: white; "
            + "-fx-border-radius: 8px; -fx-background-radius: 8px; "
            + "-fx-cursor: hand; -fx-font-weight: 700;"
        );
        bookBtn.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume(); // Prevent dialog from closing immediately

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Book Flight");
            dialog.setHeaderText("Complete your booking");
            dialog.setContentText("Please enter passenger name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(passengerName -> {
                BookingService bookingService = new BookingService();
                try {
                    Booking booking = bookingService.bookFlight(passengerName, itinerary);
                    if (booking != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Booking Confirmed");
                        alert.setHeaderText("Successfully Booked!");
                        alert.setContentText("Booking ID: " + booking.getBookingId() + "\nPassenger: " + booking.getPassengerName());
                        alert.showAndWait();
                        
                        // Close the flight detail dialog on success
                        FlightDetailDialog.this.setResult(null);
                        FlightDetailDialog.this.close();
                    }
                } catch (IllegalArgumentException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Booking Failed");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            });
        });
    }

    private HBox buildJourneySummary(FlightItinerary itinerary) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        row.setStyle("-fx-background-color: " + PANEL_BG + "; -fx-background-radius: 10px; "
                + "-fx-padding: 16; -fx-border-color: " + BORDER_COLOR + "; "
                + "-fx-border-radius: 10px; -fx-border-width: 1px;");

        if (itinerary.getSegments().isEmpty()) return row;

        var first = itinerary.getSegments().get(0);
        var last  = itinerary.getSegments().get(itinerary.getSegments().size() - 1);

        VBox dep = timeBlock(
            first.getDepartureAirport().getAirportCode(),
            DateTimeUtils.formatDateTime(itinerary.getDepartureTime(), "HH:mm"),
            first.getDepartureAirport().getCity() != null ? first.getDepartureAirport().getCity() : "");

        VBox arr = timeBlock(
            last.getArrivalAirport().getAirportCode(),
            DateTimeUtils.formatDateTime(itinerary.getArrivalTime(), "HH:mm"),
            last.getArrivalAirport().getCity() != null ? last.getArrivalAirport().getCity() : "");

        String durStr = DateTimeUtils.formatDuration(itinerary.getTotalTravelTime());
        boolean isDirect = itinerary.getNumberOfStops() == 0;

        VBox mid = new VBox(4);
        mid.setAlignment(Pos.CENTER);
        Label durationLbl = new Label(durStr);
        durationLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_ACCENT + "; -fx-font-weight: 600;");
        Label arrowLbl = new Label("──✈──");
        arrowLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ACCENT_BLUE + ";");
        Label stopLbl = new Label(isDirect ? "● DIRECT" : "● " + itinerary.getNumberOfStops() + " STOP");
        stopLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: "
                + (isDirect ? ACCENT_GREEN : ACCENT_ORANGE) + ";");
        mid.getChildren().addAll(durationLbl, arrowLbl, stopLbl);

        Region r1 = new Region(); HBox.setHgrow(r1, Priority.ALWAYS);
        Region r2 = new Region(); HBox.setHgrow(r2, Priority.ALWAYS);
        row.getChildren().addAll(dep, r1, mid, r2, arr);
        return row;
    }

    private VBox timeBlock(String code, String time, String city) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 800; -fx-text-fill: " + TEXT_PRIMARY + ";");
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_ACCENT + ";");
        Label cityLabel = new Label(city.toUpperCase());
        cityLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-weight: 600; -fx-letter-spacing: 1px;");
        box.getChildren().addAll(codeLabel, timeLabel, cityLabel);
        return box;
    }

    private VBox createSegmentView(FlightSegment segment) {
        VBox box = new VBox(6);
        box.setStyle("-fx-background-color: " + PANEL_BG + "; -fx-padding: 14; "
                + "-fx-background-radius: 10px; -fx-border-color: " + BORDER_COLOR + "; "
                + "-fx-border-radius: 10px; -fx-border-width: 1px;");

        Label route = new Label(String.format("%s (%s)  →  %s (%s)",
            segment.getDepartureAirport().getCity(), segment.getDepartureAirport().getAirportCode(),
            segment.getArrivalAirport().getCity(), segment.getArrivalAirport().getAirportCode()));
        route.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: " + TEXT_PRIMARY + ";");

        Label times = new Label(String.format("%s  →  %s",
            DateTimeUtils.formatDateTime(segment.getDepartureTime(), DateTimeUtils.DISPLAY_TIME_FORMAT),
            DateTimeUtils.formatDateTime(segment.getArrivalTime(), DateTimeUtils.DISPLAY_TIME_FORMAT)));
        times.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_ACCENT + ";");

        Label duration = new Label("Duration: " + DateTimeUtils.formatDuration(segment.getFlightDuration()));
        duration.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        Label aircraft = new Label("Aircraft: " + (segment.getAircraft() != null ? segment.getAircraft() : "N/A"));
        aircraft.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        Label flightNo = new Label("Flight: " + segment.getFlightNumber());
        flightNo.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        box.getChildren().addAll(route, times, duration, aircraft, flightNo);
        return box;
    }

    private HBox createLayoverView(Layover layover) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(6, 16, 6, 16));

        Label icon = new Label("⧗");
        icon.setStyle("-fx-text-fill: " + ACCENT_ORANGE + "; -fx-font-size: 13px;");

        Label label = new Label(String.format("Layover at %s (%s)  ·  %s",
            layover.getLayoverAirport().getCity(),
            layover.getLayoverAirport().getAirportCode(),
            DateTimeUtils.formatDuration(layover.getLayoverDuration())));
        label.setStyle("-fx-text-fill: " + ACCENT_ORANGE + "; -fx-font-size: 11px; -fx-font-weight: 600;");

        box.getChildren().addAll(icon, label);
        return box;
    }

    private VBox extraCard(String title, String value, String valueColor) {
        VBox card = new VBox(3);
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED
                + "; -fx-font-weight: 700; -fx-letter-spacing: 0.8px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + valueColor + "; -fx-font-weight: 700;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
}
