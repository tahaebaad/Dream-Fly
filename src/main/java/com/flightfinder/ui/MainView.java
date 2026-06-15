package com.flightfinder.ui;

import com.flightfinder.model.FlightItinerary;
import com.flightfinder.model.SearchCriteria;
import com.flightfinder.service.FlightSearchService;
import com.flightfinder.util.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MainView {

    // ── FXML bindings ──────────────────────────────────────────────
    @FXML private ComboBox<String>  originCombo;
    @FXML private ComboBox<String>  destinationCombo;
    @FXML private DatePicker        departureDate;
    @FXML private DatePicker        returnDate;
    @FXML private Spinner<Integer>  passengersSpinner;
    @FXML private CheckBox          oneWayCheck;
    @FXML private Button            searchButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label             statusLabel;
    @FXML private VBox              resultsContainer;
    @FXML private VBox              filterContainer;
    @FXML private ComboBox<String>  sortCombo;

    @FXML private Button btnRoundTrip;
    @FXML private Button btnOneWay;
    @FXML private Button btnMultiCity;

    @FXML private Label passengerLabel;
    @FXML private Button btnDecPassenger;
    @FXML private Button btnIncPassenger;

    @FXML private Button btnEconomy;
    @FXML private Button btnPremium;
    @FXML private Button btnBusiness;
    @FXML private Button btnFirst;

    @FXML private VBox returnDateBox;
    
    private int passengerCount = 1;

    // ── Internal state ─────────────────────────────────────────────
    private ResultsPanel  resultsPanel;
    private FilterPanel   filterPanel;
    private final FlightSearchService searchService;
    private List<FlightItinerary>     currentResults;

    // Currently selected cabin class (driven by the toolbar buttons in FXML)
    private String selectedCabinClass = "ECONOMY";

    public MainView(FlightSearchService searchService) {
        this.searchService = searchService;
    }

    @FXML
    public void initialize() {
        setupSearchForm();
        setupComponents();
    }

    private void setupSearchForm() {
        // ── Airports ──
        java.util.Map<String, String> airports = com.flightfinder.util.AirportDictionary.getMajorAirports();
        List<String> formatted = airports.entrySet().stream()
                .map(e -> e.getKey() + " - " + e.getValue())
                .collect(Collectors.toList());

        originCombo.getItems().addAll(formatted);
        destinationCombo.getItems().addAll(formatted);

        originCombo.setValue("LHR - London, UK");
        destinationCombo.setValue("JFK - New York, USA");

        // ── Dates ──
        departureDate.setValue(LocalDate.now().plusDays(7));

        // ── Passengers ──
        passengersSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 1));

        // ── Sort ──
        sortCombo.getItems().addAll(
                "Best Value",
                "Price (Low to High)",
                "Duration (Shortest)",
                "Stops (Fewest)");
        sortCombo.getSelectionModel().selectFirst();
        sortCombo.setOnAction(e -> applySort());
    }

    private void setupComponents() {
        resultsPanel = new ResultsPanel();
        filterPanel  = new FilterPanel();

        // Insert results panel at the top of the container (sort bar stays below via FXML)
        resultsContainer.getChildren().add(0, resultsPanel);
        VBox.setVgrow(resultsPanel, Priority.ALWAYS);
        filterContainer.getChildren().add(filterPanel);
        
        filterPanel.setOnFilterChanged(() -> applySort());
    }

    @FXML
    private void handleSearch() {
        SearchCriteria criteria = new SearchCriteria();

        // Parse IATA codes
        String originRaw = originCombo.getValue();
        String destRaw   = destinationCombo.getValue();
        String originCode = (originRaw != null && originRaw.contains(" - "))
                ? originRaw.split(" - ")[0] : originRaw;
        String destCode = (destRaw != null && destRaw.contains(" - "))
                ? destRaw.split(" - ")[0] : destRaw;

        criteria.setOriginAirportCode(originCode);
        criteria.setDestinationAirportCode(destCode);
        criteria.setDepartureDate(departureDate.getValue());

        if (oneWayCheck != null && !oneWayCheck.isSelected() && returnDate != null) {
            criteria.setReturnDate(returnDate.getValue());
        }

        criteria.setNumberOfPassengers(passengerCount);
        criteria.setCabinClass(selectedCabinClass);

        // ── UI: searching state ──
        if (searchButton != null) searchButton.setDisable(true);
        if (progressIndicator != null) progressIndicator.setVisible(true);
        if (statusLabel != null) statusLabel.setText("Searching flights…");

        // ── Async search ──
        CompletableFuture.supplyAsync(() -> {
            try {
                return searchService.searchFlights(criteria);
            } catch (Throwable e) {
                Logger.logError("Search failed", new Exception(e));
                throw new RuntimeException(e);
            }
        }).thenAccept(results -> Platform.runLater(() -> {
            try {
                currentResults = results;
                updateResultsTable(results);
                String routeText = originCode + " → " + destCode + "  ·  "
                        + results.size() + " flights found  ·  " + selectedCabinClass + "  ·  "
                        + passengerCount
                        + " passenger(s)";
                if (statusLabel != null) statusLabel.setText(routeText);
            } catch (Throwable e) {
                Logger.logError("UI update failed", new Exception(e));
                if (statusLabel != null) statusLabel.setText("Error displaying results");
                new Alert(Alert.AlertType.ERROR, "Failed to display results: " + e.getMessage()).showAndWait();
            } finally {
                if (searchButton != null) searchButton.setDisable(false);
                if (progressIndicator != null) progressIndicator.setVisible(false);
            }
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                if (searchButton != null) searchButton.setDisable(false);
                if (progressIndicator != null) progressIndicator.setVisible(false);
                String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                if (statusLabel != null) statusLabel.setText("Error: " + msg);
                new Alert(Alert.AlertType.ERROR, msg).showAndWait();
            });
            return null;
        });
    }

    private void updateResultsTable(List<FlightItinerary> results) {
        if (results == null) return;
        List<FlightItinerary> filtered = results.stream()
                .filter(f -> f.getTotalPrice() <= filterPanel.getMaxPrice())
                .filter(f -> f.getNumberOfStops() <= filterPanel.getMaxStops())
                .filter(f -> filterPanel.acceptsDepartureTime(f.getDepartureTime()))
                .filter(f -> {
                    if (f.getSegments() == null || f.getSegments().isEmpty()) return true;
                    return filterPanel.acceptsAirline(f.getSegments().get(0).getAirline().getAirlineName());
                })
                .collect(Collectors.toList());

        String sort = sortCombo.getValue();
        if (sort != null) {
            com.flightfinder.service.FlightSortService sortContext = searchService.getSortService();
            if (sort.contains("Price")) {
                sortContext.setSortStrategy(new com.flightfinder.service.PriceSortStrategy());
            } else if (sort.contains("Duration")) {
                sortContext.setSortStrategy(new com.flightfinder.service.DurationSortStrategy());
            } else if (sort.contains("Stops")) {
                sortContext.setSortStrategy(new com.flightfinder.service.StopsSortStrategy());
            } else {
                sortContext.setSortStrategy(null);
            }
            sortContext.sortFlights(filtered);
        }

        resultsPanel.updateItems(filtered);
    }

    private void applySort() {
        if (currentResults != null) {
            updateResultsTable(currentResults);
        }
    }

    @FXML
    public void handleRoundTrip() {
        btnRoundTrip.getStyleClass().setAll("button", "trip-type-btn-active");
        btnOneWay.getStyleClass().setAll("button", "trip-type-btn");
        btnMultiCity.getStyleClass().setAll("button", "trip-type-btn");
        if (returnDateBox != null) {
            returnDateBox.setVisible(true);
            returnDateBox.setManaged(true);
        }
        if (oneWayCheck != null) oneWayCheck.setSelected(false);
    }

    @FXML
    public void handleOneWay() {
        btnRoundTrip.getStyleClass().setAll("button", "trip-type-btn");
        btnOneWay.getStyleClass().setAll("button", "trip-type-btn-active");
        btnMultiCity.getStyleClass().setAll("button", "trip-type-btn");
        if (returnDateBox != null) {
            returnDateBox.setVisible(false);
            returnDateBox.setManaged(false);
        }
        if (oneWayCheck != null) oneWayCheck.setSelected(true);
    }

    @FXML
    public void handleMultiCity() {
        btnRoundTrip.getStyleClass().setAll("button", "trip-type-btn");
        btnOneWay.getStyleClass().setAll("button", "trip-type-btn");
        btnMultiCity.getStyleClass().setAll("button", "trip-type-btn-active");
        if (returnDateBox != null) {
            returnDateBox.setVisible(true);
            returnDateBox.setManaged(true);
        }
        if (oneWayCheck != null) oneWayCheck.setSelected(false);
    }

    @FXML
    public void decreasePassengers() {
        if (passengerCount > 1) {
            passengerCount--;
            if (passengerLabel != null) passengerLabel.setText(String.valueOf(passengerCount));
        }
    }

    @FXML
    public void increasePassengers() {
        if (passengerCount < 9) {
            passengerCount++;
            if (passengerLabel != null) passengerLabel.setText(String.valueOf(passengerCount));
        }
    }

    @FXML
    public void selectEconomy() {
        updateCabinClassStyles(btnEconomy);
        selectedCabinClass = "ECONOMY";
    }

    @FXML
    public void selectPremium() {
        updateCabinClassStyles(btnPremium);
        selectedCabinClass = "PREMIUM_ECONOMY";
    }

    @FXML
    public void selectBusiness() {
        updateCabinClassStyles(btnBusiness);
        selectedCabinClass = "BUSINESS";
    }

    @FXML
    public void selectFirst() {
        updateCabinClassStyles(btnFirst);
        selectedCabinClass = "FIRST";
    }

    private void updateCabinClassStyles(Button activeBtn) {
        if (btnEconomy != null) btnEconomy.getStyleClass().setAll("button", "class-btn");
        if (btnPremium != null) btnPremium.getStyleClass().setAll("button", "class-btn");
        if (btnBusiness != null) btnBusiness.getStyleClass().setAll("button", "class-btn");
        if (btnFirst != null) btnFirst.getStyleClass().setAll("button", "class-btn");
        if (activeBtn != null) activeBtn.getStyleClass().setAll("button", "class-btn-active");
    }

    /** Called by cabin-class buttons in the nav bar (if wired via FXML onAction) */
    public void selectCabinClass(String cabinClass) {
        this.selectedCabinClass = cabinClass;
    }

    @FXML
    public void handleMyBookings() {
        new MyBookingsDialog().showAndWait();
    }
}
