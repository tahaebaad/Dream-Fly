package com.flightfinder.ui;

import com.flightfinder.db.BookingDAO;
import com.flightfinder.model.Booking;
import com.flightfinder.util.DateTimeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class MyBookingsDialog extends Dialog<Void> {

    private final VBox bookingsBox;
    private final com.flightfinder.service.BookingService bookingService;

    private static final String MAIN_BG      = "#FFFFFF";
    private static final String PANEL_BG     = "#F0F4F8";
    private static final String BORDER_COLOR = "#CBD5E1";
    private static final String TEXT_PRIMARY  = "#1A1A1A";
    private static final String TEXT_MUTED    = "#6B7A99";
    private static final String TEXT_ACCENT   = "#6B7A99";
    private static final String ACCENT_BLUE   = "#3772FF";

    public MyBookingsDialog() {
        setTitle("Dream Fly · My Bookings");
        setHeaderText(null);

        getDialogPane().setStyle("-fx-background-color: " + MAIN_BG + ";");

        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: " + MAIN_BG + ";");
        content.setPrefWidth(500);
        content.setPrefHeight(400);

        Label title = new Label("My Saved Bookings");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: " + TEXT_PRIMARY + ";");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        this.bookingService = new com.flightfinder.service.BookingService();
        this.bookingsBox = new VBox(12);
        this.bookingsBox.setStyle("-fx-background-color: transparent;");

        refreshBookings();

        scrollPane.setContent(bookingsBox);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(title, scrollPane);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        getDialogPane().lookupButton(ButtonType.CLOSE).setStyle(
            "-fx-background-color: #FFFFFF; -fx-text-fill: #1A1A1A; "
            + "-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 8px; "
            + "-fx-background-radius: 8px; -fx-cursor: hand; -fx-font-weight: 700;"
        );
    }
    
    private void refreshBookings() {
        bookingsBox.getChildren().clear();
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings == null || bookings.isEmpty()) {
            Label emptyLabel = new Label("You have no saved bookings.");
            emptyLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px;");
            bookingsBox.getChildren().add(emptyLabel);
        } else {
            for (Booking b : bookings) {
                bookingsBox.getChildren().add(UIComponentFactory.createBookingCard(b, id -> {
                    bookingService.cancelBooking(id);
                    refreshBookings();
                }));
            }
        }
    }
}
