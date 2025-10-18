package com.example.demo.view.ui;

import com.example.demo.controller.SettingsControllers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;

public class SettingsView {
    private final SettingsControllers controller;
    private final StackPane rootStack;
    private final VBox uiBox;
    private Button backButton;
    private ScaleTransition activeButtonPulse;

    public SettingsView(SettingsControllers controller) {
        this.controller = controller;
        this.rootStack = new StackPane();
        this.uiBox = new VBox(20);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        setupBackground();
        buildUI();
    }

    private void setupBackground() {
        Region bgRegion = new Region();
        BackgroundFill fill = new BackgroundFill(
                new javafx.scene.paint.LinearGradient(0, 0, 0, 1, true,
                        javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, Color.web("#0b3a62")),
                        new javafx.scene.paint.Stop(1, Color.web("#04263a"))),
                CornerRadii.EMPTY, Insets.EMPTY);
        bgRegion.setBackground(new Background(fill));

        bgRegion.prefWidthProperty().bind(rootStack.widthProperty());
        bgRegion.prefHeightProperty().bind(rootStack.heightProperty());

        rootStack.getChildren().add(bgRegion);
        StackPane.setAlignment(bgRegion, Pos.CENTER);
    }

    private void buildUI() {
        Text title = new Text("Audio Settings");
        title.setFont(Font.font(34));
        title.setFill(Color.WHITE);

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(15);
        settingsGrid.setAlignment(Pos.CENTER);

        // Music Volume Control
        Label musicLabel = new Label("Music Volume:");
        musicLabel.setTextFill(Color.WHITE);
        Slider musicSlider = new Slider(0, 1, controller.getModel().getMusicVolume());
        musicSlider.setStyle("-fx-control-inner-background: #2a6cff;");
        musicSlider.valueProperty().bindBidirectional(controller.getModel().musicVolumeProperty());

        // Effects Volume Control
        Label effectsLabel = new Label("Effects Volume:");
        effectsLabel.setTextFill(Color.WHITE);
        Slider effectsSlider = new Slider(0, 1, controller.getModel().getEffectVolume());
        effectsSlider.setStyle("-fx-control-inner-background: #2a6cff;");
        effectsSlider.valueProperty().bindBidirectional(controller.getModel().effectVolumeProperty());

        // Enable/Disable Controls
        CheckBox musicToggle = new CheckBox("Enable Music");
        musicToggle.setTextFill(Color.WHITE);
        musicToggle.selectedProperty().bindBidirectional(controller.getModel().musicEnabledProperty());

        CheckBox effectsToggle = new CheckBox("Enable Sound Effects");
        effectsToggle.setTextFill(Color.WHITE);
        effectsToggle.selectedProperty().bindBidirectional(controller.getModel().effectEnabledProperty());

        // Layout
        settingsGrid.add(musicLabel, 0, 0);
        settingsGrid.add(musicSlider, 1, 0);
        settingsGrid.add(effectsLabel, 0, 1);
        settingsGrid.add(effectsSlider, 1, 1);
        settingsGrid.add(musicToggle, 0, 2, 2, 1);
        settingsGrid.add(effectsToggle, 0, 3, 2, 1);

        // Back Button
        backButton = new Button("Back to Menu");
        backButton.setMinWidth(220);
        backButton.setFocusTraversable(false);
        backButton.setFont(Font.font(18));
        backButton.setStyle("-fx-background-color: linear-gradient(#6aa0ff, #2a6cff); -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> {
            System.out.println("🟢 Button clicked!");
            controller.backToMenu();
        });


        // Add pulse animation on hover
        backButton.setOnMouseEntered(e -> startPulse());
        backButton.setOnMouseExited(e -> stopPulse());

        uiBox.getChildren().addAll(title, settingsGrid, backButton);
        rootStack.getChildren().add(uiBox);
    }

    private void startPulse() {
        if (activeButtonPulse != null) {
            activeButtonPulse.stop();
        }
        activeButtonPulse = new ScaleTransition(Duration.millis(420), backButton);
        activeButtonPulse.setFromX(1.0);
        activeButtonPulse.setFromY(1.0);
        activeButtonPulse.setToX(1.06);
        activeButtonPulse.setToY(1.06);
        activeButtonPulse.setCycleCount(ScaleTransition.INDEFINITE);
        activeButtonPulse.setAutoReverse(true);
        activeButtonPulse.play();
    }

    private void stopPulse() {
        if (activeButtonPulse != null) {
            activeButtonPulse.stop();
            backButton.setScaleX(1.0);
            backButton.setScaleY(1.0);
        }
    }

    public void enableKeyboard(Scene scene) {
        scene.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                controller.backToMenu();
                ev.consume();
            }
        });
    }

    public Node getRoot() {
        return rootStack;
    }
}