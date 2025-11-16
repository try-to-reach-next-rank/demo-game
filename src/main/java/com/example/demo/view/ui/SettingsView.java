package com.example.demo.view.ui;

import com.example.demo.controller.view.SettingsController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.ui.AbstractUIView;
import com.example.demo.model.menu.ButtonManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SettingsView extends AbstractUIView {
    private final SettingsController controller;
    private final VBox uiBox;
    private final ButtonManager buttonManager;

    public SettingsView(SettingsController controller) {
        buttonManager = new ButtonManager(themeController.getHandImage());
        this.controller = controller;
        this.uiBox = new VBox(20);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        buildUI();

        root.getChildren().add(uiBox);
        StackPane.setAlignment(uiBox, Pos.CENTER);
    }

    @Override
    public void buildUI() {
        Text title = new Text("Audio Settings");
        title.setFont(Font.font(34));
        title.setFill(Color.WHITE);

        // Settings grid với layout mới
        GridPane settingsGrid = createSettingsGrid();

        // Back button
        HBox backRow = buttonManager.createButtonRow(
                "Back to Menu",
                e -> controller.backToMenu()
        );
        uiBox.getChildren().addAll(title, settingsGrid, backRow);
    }

    private GridPane createSettingsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));

        // ===== MUSIC ROW =====
        // Label
        Label musicLabel = new Label("Music:");
        musicLabel.setTextFill(Color.WHITE);
        musicLabel.setFont(Font.font(16));
        musicLabel.setMinWidth(80);

        // Slider
        Slider musicSlider = new Slider(0, 1, controller.getModel().getMusicVolume());
        musicSlider.setStyle("-fx-control-inner-background: #2a6cff;");
        musicSlider.setPrefWidth(250);
        musicSlider.valueProperty().bindBidirectional(controller.getModel().musicVolumeProperty());

        // Enable checkbox bên cạnh slider
        CheckBox musicToggle = new CheckBox("Enable");
        musicToggle.setTextFill(Color.WHITE);
        musicToggle.selectedProperty().bindBidirectional(controller.getModel().musicEnabledProperty());

        // Disable slider khi checkbox unchecked
        musicSlider.disableProperty().bind(musicToggle.selectedProperty().not());

        // ===== EFFECTS ROW =====
        // Label
        Label effectsLabel = new Label("Effects:");
        effectsLabel.setTextFill(Color.WHITE);
        effectsLabel.setFont(Font.font(16));
        effectsLabel.setMinWidth(80);

        // Slider
        Slider effectsSlider = new Slider(0, 1, controller.getModel().getEffectVolume());
        effectsSlider.setStyle("-fx-control-inner-background: #2a6cff;");
        effectsSlider.setPrefWidth(250);
        effectsSlider.valueProperty().bindBidirectional(controller.getModel().effectVolumeProperty());

        // Enable checkbox bên cạnh slider
        CheckBox effectsToggle = new CheckBox("Enable");
        effectsToggle.setTextFill(Color.WHITE);
        effectsToggle.selectedProperty().bindBidirectional(controller.getModel().effectEnabledProperty());

        // Disable slider khi checkbox unchecked
        effectsSlider.disableProperty().bind(effectsToggle.selectedProperty().not());

        // ===== LAYOUT =====
        // Row 0: Music Label | Slider | Enable Checkbox
        grid.add(musicLabel, 0, 0);
        grid.add(musicSlider, 1, 0);
        grid.add(musicToggle, 2, 0);

        // Row 1: Effects Label | Slider | Enable Checkbox
        grid.add(effectsLabel, 0, 1);
        grid.add(effectsSlider, 1, 1);
        grid.add(effectsToggle, 2, 1);

        // Alignment
        GridPane.setHalignment(musicLabel, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(effectsLabel, javafx.geometry.HPos.RIGHT);
        GridPane.setHalignment(musicToggle, javafx.geometry.HPos.LEFT);
        GridPane.setHalignment(effectsToggle, javafx.geometry.HPos.LEFT);

        // Optional: Add visual feedback panel showing current values
        VBox infoPanel = createInfoPanel(musicSlider, effectsSlider, musicToggle, effectsToggle);
        grid.add(infoPanel, 0, 2, 3, 1);
        GridPane.setHalignment(infoPanel, javafx.geometry.HPos.CENTER);
        GridPane.setMargin(infoPanel, new Insets(20, 0, 0, 0));

        return grid;
    }

    /**
     * Tạo panel hiển thị giá trị hiện tại (optional - có thể bỏ nếu không cần)
     */
    private VBox createInfoPanel(Slider musicSlider, Slider effectsSlider,
                                 CheckBox musicToggle, CheckBox effectsToggle) {
        VBox panel = new VBox(8);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-background-radius: 10;");

        Label musicInfo = new Label();
        musicInfo.setTextFill(Color.WHITE);
        musicInfo.setFont(Font.font(14));

        Label effectInfo = new Label();
        effectInfo.setTextFill(Color.WHITE);
        effectInfo.setFont(Font.font(14));

        // Update labels khi slider thay đổi
        musicSlider.valueProperty().addListener((obs, oldV, newV) -> {
            String status = musicToggle.isSelected() ? "ON" : "OFF";
            musicInfo.setText(String.format("Music: %d%% [%s]",
                    (int)(newV.doubleValue() * 100), status));
        });

        effectsSlider.valueProperty().addListener((obs, oldV, newV) -> {
            String status = effectsToggle.isSelected() ? "ON" : "OFF";
            effectInfo.setText(String.format("Effects: %d%% [%s]",
                    (int)(newV.doubleValue() * 100), status));
        });

        // Update labels khi checkbox thay đổi
        musicToggle.selectedProperty().addListener((obs, oldV, newV) -> {
            String status = newV ? "ON" : "OFF";
            musicInfo.setText(String.format("Music: %d%% [%s]",
                    (int)(musicSlider.getValue() * 100), status));
        });

        effectsToggle.selectedProperty().addListener((obs, oldV, newV) -> {
            String status = newV ? "ON" : "OFF";
            effectInfo.setText(String.format("Effects: %d%% [%s]",
                    (int)(effectsSlider.getValue() * 100), status));
        });

        // Initial values
        musicInfo.setText(String.format("Music: %d%% [%s]",
                (int)(musicSlider.getValue() * 100),
                musicToggle.isSelected() ? "ON" : "OFF"));
        effectInfo.setText(String.format("Effects: %d%% [%s]",
                (int)(effectsSlider.getValue() * 100),
                effectsToggle.isSelected() ? "ON" : "OFF"));

        panel.getChildren().addAll(musicInfo, effectInfo);
        return panel;
    }

    @Override
    public void handleInput(KeyCode keyCode) {
        if (keyCode == KeyCode.ESCAPE) {
            controller.backToMenu();
        }
    }


    @Override
    public void moveUp() {

    }

    @Override
    public void moveDown() {

    }

    @Override
    public void moveLeft() {

    }

    @Override
    public void moveRight() {

    }

    @Override
    public void confirm() {

    }

    @Override
    public void cancel() {

    }
}