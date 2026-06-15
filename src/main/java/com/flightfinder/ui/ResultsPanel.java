package com.flightfinder.ui;

import com.flightfinder.model.FlightItinerary;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.List;

public class ResultsPanel extends VBox {

    private ListView<FlightItinerary> resultsList;

    public ResultsPanel() {
        resultsList = new ListView<>();
        resultsList.setFocusTraversable(false); // Remove focus border clutter
        VBox.setVgrow(resultsList, Priority.ALWAYS);
        
        setupListView();
        this.getChildren().add(resultsList);
        
        // Modern styling
        this.getStyleClass().add("results-panel");
        resultsList.getStyleClass().add("flight-list");
    }

    private void setupListView() {
        resultsList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FlightItinerary> call(ListView<FlightItinerary> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(FlightItinerary item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                            setStyle("-fx-background-color: transparent;");
                        } else {
                            setGraphic(UIComponentFactory.createFlightCard(item));
                            setText(null);
                            // Ensure the cell itself is transparent so card styling takes over
                            setStyle("-fx-background-color: transparent; -fx-padding: 5px;"); 
                        }
                    }
                };
            }
        });
    }
    
    // Compatibility method for MainView (simulating table items set)
    // Ideally we should refactor MainView to use a more generic update method but this keeps signature close
    public void updateItems(List<FlightItinerary> flights) {
        resultsList.setItems(FXCollections.observableArrayList(flights));
    }
    
    // Deprecated access, keep for now if needed or remove call in MainView
    public ListView<FlightItinerary> getList() {
        return resultsList;
    }
}
