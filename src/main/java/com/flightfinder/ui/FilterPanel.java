package com.flightfinder.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FilterPanel extends VBox {

    private RadioButton anyStopRadio;
    private RadioButton directOnlyRadio;
    private RadioButton oneStopRadio;

    private Slider maxPriceSlider;
    private Label  maxPriceLabel;
    private Label  minPriceLabel;

    // Departure time toggle buttons
    private Label earlyAmBtn;
    private Label morningBtn;
    private Label afternoonBtn;
    private Label eveningBtn;
    private boolean earlyAmActive    = false;
    private boolean morningActive    = true;
    private boolean afternoonActive  = false;
    private boolean eveningActive    = false;

    private CheckBox emiratesCb;
    private CheckBox piaCb;
    private CheckBox qatarCb;
    private CheckBox britishCb;
    private CheckBox turkishCb;

    private Runnable onFilterChanged;

    public void setOnFilterChanged(Runnable onFilterChanged) {
        this.onFilterChanged = onFilterChanged;
    }

    private void notifyChange() {
        if (onFilterChanged != null) {
            onFilterChanged.run();
        }
    }

    public FilterPanel() {
        setSpacing(0);
        setPadding(new Insets(0));
        getStyleClass().add("filter-panel");

        // ── Header ──────────────────────────────────────────
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));

        Label filtersLabel = new Label("FILTERS");
        filtersLabel.getStyleClass().add("filter-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label resetBtn = new Label("RESET ALL");
        resetBtn.getStyleClass().add("filter-reset");
        resetBtn.setStyle("-fx-text-fill: #3772FF; -fx-font-size: 11px; -fx-font-weight: 600; -fx-cursor: hand;");
        resetBtn.setOnMouseClicked(e -> resetAll());

        header.getChildren().addAll(filtersLabel, spacer, resetBtn);

        // ── Stops ────────────────────────────────────────────
        VBox stopsBox = new VBox(8);
        stopsBox.setPadding(new Insets(0, 0, 18, 0));

        Label stopsTitle = new Label("STOPS");
        stopsTitle.getStyleClass().add("filter-section-title");

        ToggleGroup stopsGroup = new ToggleGroup();

        anyStopRadio    = styledRadio("Any stops",    stopsGroup);
        directOnlyRadio = styledRadio("Direct only",  stopsGroup);
        oneStopRadio    = styledRadio("Max 1 stop",   stopsGroup);
        anyStopRadio.setSelected(true);
        
        stopsGroup.selectedToggleProperty().addListener((obs, old, newVal) -> notifyChange());

        // Counts (decorative)
        HBox anyRow    = radioRow(anyStopRadio,    "124");
        HBox directRow = radioRow(directOnlyRadio, "38");
        HBox oneRow    = radioRow(oneStopRadio,    "96");

        stopsBox.getChildren().addAll(stopsTitle, anyRow, directRow, oneRow);

        // ── Separator ─────────────────────────────────────────
        Separator sep1 = darkSeparator();

        // ── Max Price ─────────────────────────────────────────
        VBox priceBox = new VBox(8);
        priceBox.setPadding(new Insets(18, 0, 18, 0));

        Label priceTitle = new Label("MAX PRICE");
        priceTitle.getStyleClass().add("filter-section-title");

        maxPriceSlider = new Slider(100, 5000, 1500);
        maxPriceSlider.getStyleClass().add("slider");
        maxPriceSlider.setPrefWidth(210);

        HBox priceRange = new HBox();
        priceRange.setAlignment(Pos.CENTER_LEFT);
        minPriceLabel = new Label("from $300");
        minPriceLabel.setStyle("-fx-text-fill: #6B7A99; -fx-font-size: 11px;");
        maxPriceLabel = new Label("$1,500");
        maxPriceLabel.setStyle("-fx-text-fill: #FFB547; -fx-font-size: 14px; -fx-font-weight: 800;");

        Region priceSpacer = new Region(); HBox.setHgrow(priceSpacer, Priority.ALWAYS);
        Label perPerson = new Label("per person");
        perPerson.setStyle("-fx-text-fill: #6B7A99; -fx-font-size: 10px;");

        VBox maxPriceStack = new VBox(1);
        maxPriceStack.setAlignment(Pos.CENTER_RIGHT);
        maxPriceStack.getChildren().addAll(maxPriceLabel, perPerson);

        priceRange.getChildren().addAll(minPriceLabel, priceSpacer, maxPriceStack);

        maxPriceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double v = newVal.doubleValue();
            maxPriceLabel.setText(String.format("$%,.0f", v));
            notifyChange();
        });

        priceBox.getChildren().addAll(priceTitle, priceRange, maxPriceSlider);

        // ── Separator ─────────────────────────────────────────
        Separator sep2 = darkSeparator();

        // ── Departure Time ────────────────────────────────────
        VBox departureBox = new VBox(10);
        departureBox.setPadding(new Insets(18, 0, 18, 0));

        Label departureTitle = new Label("DEPARTURE");
        departureTitle.getStyleClass().add("filter-section-title");

        // 2x2 grid of time toggles
        earlyAmBtn   = timeToggle("🌙", "EARLY AM",   earlyAmActive);
        morningBtn   = timeToggle("🌅", "MORNING",    morningActive);
        afternoonBtn = timeToggle("☀", "AFTERNOON",  afternoonActive);
        eveningBtn   = timeToggle("🌆", "EVENING",    eveningActive);

        wireToggle(earlyAmBtn,   () -> { earlyAmActive   = !earlyAmActive;   refreshToggles(); notifyChange(); });
        wireToggle(morningBtn,   () -> { morningActive   = !morningActive;   refreshToggles(); notifyChange(); });
        wireToggle(afternoonBtn, () -> { afternoonActive = !afternoonActive; refreshToggles(); notifyChange(); });
        wireToggle(eveningBtn,   () -> { eveningActive   = !eveningActive;   refreshToggles(); notifyChange(); });

        HBox timeRow1 = new HBox(6, earlyAmBtn, morningBtn);
        HBox timeRow2 = new HBox(6, afternoonBtn, eveningBtn);

        departureBox.getChildren().addAll(departureTitle, timeRow1, timeRow2);

        // ── Separator ─────────────────────────────────────────
        Separator sep3 = darkSeparator();

        // ── Airlines ──────────────────────────────────────────
        VBox airlinesBox = new VBox(8);
        airlinesBox.setPadding(new Insets(18, 0, 8, 0));

        Label airlinesTitle = new Label("AIRLINES");
        airlinesTitle.getStyleClass().add("filter-section-title");

        emiratesCb = darkCheck("Emirates",         "$920");
        piaCb      = darkCheck("PIA",              "$780");
        qatarCb    = darkCheck("Qatar Airways",    "$1105");
        britishCb  = darkCheck("British Airways",  "$910");
        turkishCb  = darkCheck("Turkish Airlines", "$950");

        airlinesBox.getChildren().addAll(airlinesTitle,
                emiratesCb, piaCb, qatarCb, britishCb, turkishCb);

        // ── Assemble ──────────────────────────────────────────
        getChildren().addAll(header, stopsBox, sep1, priceBox, sep2, departureBox, sep3, airlinesBox);
    }

    // ── Helpers ────────────────────────────────────────────────

    private RadioButton styledRadio(String text, ToggleGroup group) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(group);
        rb.getStyleClass().add("radio-button");
        return rb;
    }

    private HBox radioRow(RadioButton radio, String count) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(count);
        badge.getStyleClass().add("filter-count");
        row.getChildren().addAll(radio, spacer, badge);
        return row;
    }

    private Separator darkSeparator() {
        Separator sep = new Separator();
        sep.getStyleClass().add("separator");
        sep.setStyle("-fx-opacity: 0.4;");
        return sep;
    }

    private Label timeToggle(String icon, String label, boolean active) {
        Label btn = new Label(icon + "\n" + label);
        btn.setAlignment(Pos.CENTER);
        btn.setStyle(active ? activeTimeStyle() : inactiveTimeStyle());
        btn.setPrefWidth(100);
        btn.setPrefHeight(52);
        btn.setWrapText(false);
        btn.setStyle((active ? activeTimeStyle() : inactiveTimeStyle())
                + " -fx-alignment: center; -fx-text-alignment: center;");
        return btn;
    }

    private void wireToggle(Label btn, Runnable onToggle) {
        btn.setOnMouseClicked(e -> onToggle.run());
    }

    private void refreshToggles() {
        applyToggleStyle(earlyAmBtn,   earlyAmActive);
        applyToggleStyle(morningBtn,   morningActive);
        applyToggleStyle(afternoonBtn, afternoonActive);
        applyToggleStyle(eveningBtn,   eveningActive);
    }

    private void applyToggleStyle(Label btn, boolean active) {
        btn.setStyle((active ? activeTimeStyle() : inactiveTimeStyle())
                + " -fx-alignment: center; -fx-text-alignment: center;");
    }

    private String activeTimeStyle() {
        return "-fx-background-color: #F0F4F8; -fx-text-fill: #3772FF; "
                + "-fx-font-size: 11px; -fx-font-weight: 700; "
                + "-fx-background-radius: 8px; -fx-cursor: hand; "
                + "-fx-border-color: #3772FF; -fx-border-width: 1px; -fx-border-radius: 8px;";
    }

    private String inactiveTimeStyle() {
        return "-fx-background-color: #FFFFFF; -fx-text-fill: #6B7A99; "
                + "-fx-font-size: 11px; -fx-font-weight: 600; "
                + "-fx-background-radius: 8px; -fx-cursor: hand; "
                + "-fx-border-color: #E2E8F0; -fx-border-width: 1px; -fx-border-radius: 8px;";
    }

    private CheckBox darkCheck(String airline, String price) {
        CheckBox cb = new CheckBox();
        cb.setSelected(false);
        cb.getStyleClass().add("check-box");

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(airline);
        nameLabel.setStyle("-fx-text-fill: #1A1A1A; -fx-font-size: 12px;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-text-fill: #6B7A99; -fx-font-size: 11px;");

        // Wrap into a custom HBox inside checkBox graphic area
        // JavaFX CheckBox doesn't support complex graphics easily, use separate HBox
        cb.setText(airline);
        cb.setStyle("-fx-text-fill: #1A1A1A; -fx-font-size: 12px;");
        cb.selectedProperty().addListener((obs, oldV, newV) -> notifyChange());
        return cb;
    }

    private void resetAll() {
        anyStopRadio.setSelected(true);
        maxPriceSlider.setValue(5000);
        earlyAmActive = false; morningActive = true;
        afternoonActive = false; eveningActive = false;
        refreshToggles();
        if (emiratesCb != null) {
            emiratesCb.setSelected(false);
            piaCb.setSelected(false);
            qatarCb.setSelected(false);
            britishCb.setSelected(false);
            turkishCb.setSelected(false);
        }
        notifyChange();
    }

    // ── Public API ─────────────────────────────────────────────

    public boolean isDirectOnly() {
        return directOnlyRadio.isSelected();
    }

    public int getMaxStops() {
        if (directOnlyRadio.isSelected()) return 0;
        if (oneStopRadio.isSelected())    return 1;
        return Integer.MAX_VALUE;
    }

    public double getMaxPrice() {
        return maxPriceSlider.getValue();
    }

    public boolean acceptsDepartureTime(java.time.LocalDateTime depTime) {
        if (depTime == null) return true;
        
        // If nothing is selected, behave as if all are selected (don't filter out)
        if (!earlyAmActive && !morningActive && !afternoonActive && !eveningActive) return true;
        
        int h = depTime.getHour();
        if (earlyAmActive && h >= 0 && h < 6) return true;
        if (morningActive && h >= 6 && h < 12) return true;
        if (afternoonActive && h >= 12 && h < 18) return true;
        if (eveningActive && h >= 18 && h < 24) return true;
        
        return false;
    }

    public boolean acceptsAirline(String name) {
        if (name == null || name.isEmpty()) return true;
        
        boolean noneSelected = !emiratesCb.isSelected() && !piaCb.isSelected() && !qatarCb.isSelected() 
                            && !britishCb.isSelected() && !turkishCb.isSelected();
        
        if (noneSelected) {
            return true; // Use default "display all" behavior when no filters are applied
        }
        
        String n = name.toLowerCase();
        
        if (emiratesCb.isSelected() && (n.contains("emirates") || n.contains("ek"))) return true;
        if (piaCb.isSelected() && (n.contains("pia") || n.contains("pakistan"))) return true;
        if (qatarCb.isSelected() && (n.contains("qatar") || n.contains("qr"))) return true;
        if (britishCb.isSelected() && (n.contains("british") || n.contains("ba"))) return true;
        if (turkishCb.isSelected() && (n.contains("turkish") || n.contains("tk"))) return true;
        
        return false;
    }
}
