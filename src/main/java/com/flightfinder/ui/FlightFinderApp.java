package com.flightfinder.ui;

import com.flightfinder.service.FlightSearchService;
import com.flightfinder.service.api.AmadeusAPIClient;
import com.flightfinder.service.api.FlightDataCache;
import com.flightfinder.util.ConfigManager;
import com.flightfinder.util.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class FlightFinderApp extends Application {

    private FlightSearchService searchService;
    private FlightDataCache cache;
    private AmadeusAPIClient apiClient;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Check for API Keys
        checkAndPromptForAPIKeys();

        // Initialize services
        cache = new FlightDataCache();
        apiClient = new AmadeusAPIClient(cache);
        searchService = new FlightSearchService(apiClient, cache);

        // Load Main View
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/flightfinder/ui/MainView.fxml"));
        
        // Manual dependency injection for Controller
        loader.setControllerFactory(param -> {
            if (param == MainView.class) {
                return new MainView(searchService);
            }
            try {
                return param.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create controller", e);
            }
        });

        Parent root = loader.load();
        
        Scene scene = new Scene(root, 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        scene.setFill(javafx.scene.paint.Color.web("#F8F9FA"));
        
        primaryStage.setTitle("dreamfly — Flight Search");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Logger.logInfo("Application started");
    }

    private void checkAndPromptForAPIKeys() {
        ConfigManager config = ConfigManager.getInstance();
        String currentKey = config.getProperty("amadeus.api.key", "");
        String currentSecret = config.getProperty("amadeus.api.secret", "");

        if (currentKey.isEmpty() || currentKey.equals("YOUR_API_KEY") || currentKey.equals("default_key") ||
            currentSecret.isEmpty() || currentSecret.equals("YOUR_API_SECRET")) {
            
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Setup Real-Time Data");
            dialog.setHeaderText("Enable Real-Time Flight Data");
            dialog.setContentText("To search real flights, you need Amadeus API Keys.\n" +
                                "1. Go to developers.amadeus.com and register.\n" +
                                "2. Create a new App to get your API Key and Secret.\n\n" +
                                "Enter them below (or Cancel to use Mock Data):");

            // Set the button types.
            ButtonType loginButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField keyField = new TextField();
            keyField.setPromptText("API Key");
            PasswordField secretField = new PasswordField();
            secretField.setPromptText("API Secret");

            grid.add(new Label("API Key:"), 0, 0);
            grid.add(keyField, 1, 0);
            grid.add(new Label("API Secret:"), 0, 1);
            grid.add(secretField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(() -> keyField.requestFocus());

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(keyField.getText(), secretField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(keys -> {
                ConfigManager.getInstance().setProperty("amadeus.api.key", keys.getKey());
                ConfigManager.getInstance().setProperty("amadeus.api.secret", keys.getValue());
                ConfigManager.getInstance().saveConfiguration();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Configuration Saved");
                alert.setHeaderText(null);
                alert.setContentText("API Keys saved! Application will now attempt to connect to real data.");
                alert.showAndWait();
            });
        }
    }

    @Override
    public void stop() throws Exception {
        if (searchService != null) {
            searchService.shutdown();
        }
        ConfigManager.getInstance().saveConfiguration();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
