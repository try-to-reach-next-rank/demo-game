package com.example.demo.view.ui;

import com.example.demo.controller.SettingsControllers;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SettingsView {
    private final SettingsControllers controller;
    private final StackPane rootStack;
    private final VBox uiBox;

    // visual pieces similar to MenuView
    private Button backButton;
    private ScaleTransition activePulse;
    private Image handImage = null;
    private ImageView leftHand;
    private ImageView rightHand;

    public SettingsView(SettingsControllers controller) {
        this.controller = controller;
        this.rootStack = new StackPane();
        this.uiBox = new VBox(20);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        loadHandImage("/images/hand.png");         // if present in resources
        setupBackground();
        buildUI();

        // load same stylesheet as MenuView to ensure identical look
        try {
            String css = getClass().getResource("/styles/menu.css").toExternalForm();
            rootStack.getStylesheets().add(css);
        } catch (Exception ignored) {}

        // ensure hands are initially hidden
        if (leftHand != null) leftHand.setVisible(false);
        if (rightHand != null) rightHand.setVisible(false);
    }

    private void loadHandImage(String path) {
        try {
            var url = getClass().getResource(path);
            if (url != null) {
                handImage = new Image(url.toExternalForm(), true);
            }
        } catch (Exception ignored) {}
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

        // Create left/right hand imageviews (like MenuView)
        leftHand = new ImageView();
        rightHand = new ImageView();
        if (handImage != null) {
            leftHand.setImage(handImage);
            rightHand.setImage(handImage);
            rightHand.setScaleX(-1);
            leftHand.setFitWidth(28);
            leftHand.setFitHeight(28);
            leftHand.setPreserveRatio(true);
            rightHand.setFitWidth(28);
            rightHand.setFitHeight(28);
            rightHand.setPreserveRatio(true);
            leftHand.setVisible(false);
            rightHand.setVisible(false);
        } else {
            leftHand = null;
            rightHand = null;
        }

        // Back Button styled similarly (but we avoid inline style so css controls most)
        backButton = new Button("Back to Menu");
        backButton.getStyleClass().add("menu-button"); // custom class so CSS can style consistently
        backButton.setMinWidth(220);
        backButton.setFocusTraversable(false);
        backButton.setFont(Font.font(18));
        backButton.setOnAction(e -> controller.backToMenu());

        // Hover animation (same small scale on hover as MenuView)
        addHoverAnimation(backButton);

        // Mouse interactions: hovering should show hands + start pulse
        backButton.setOnMouseEntered(e -> {
            if (leftHand != null && rightHand != null) {
                leftHand.setVisible(true);
                rightHand.setVisible(true);
            }
            startPulse(backButton);
        });
        backButton.setOnMouseExited(e -> {
            if (leftHand != null && rightHand != null) {
                leftHand.setVisible(false);
                rightHand.setVisible(false);
            }
            stopPulse(backButton);
        });

        // Build row HBox with hands like MenuView's rows
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        if (leftHand != null) row.getChildren().add(leftHand);
        row.getChildren().add(backButton);
        if (rightHand != null) row.getChildren().add(rightHand);

        uiBox.getChildren().addAll(title, settingsGrid, row);
        rootStack.getChildren().add(uiBox);
    }

    // hover micro-animation (same as MenuView.addHoverAnimation)
    private void addHoverAnimation(Button b) {
        b.setOnMouseEntered(e -> {
            // small instant scale to give tactile feel
            ScaleTransition st = new ScaleTransition(Duration.millis(160), b);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        b.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(160), b);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    // pulse logic: matches MenuView's behaviour for single button
    private void startPulse(Button b) {
        // if already running, do nothing
        if (activePulse != null && activePulse.getStatus() == Animation.Status.RUNNING) return;

        // add "selected" style class so CSS appearance matches
        if (!b.getStyleClass().contains("selected")) b.getStyleClass().add("selected");

        activePulse = new ScaleTransition(Duration.millis(420), b);
        activePulse.setFromX(1.0);
        activePulse.setFromY(1.0);
        activePulse.setToX(1.06);
        activePulse.setToY(1.06);
        activePulse.setCycleCount(Timeline.INDEFINITE);
        activePulse.setAutoReverse(true);
        activePulse.play();
    }

    private void stopPulse(Button b) {
        if (activePulse != null) {
            activePulse.stop();
            activePulse = null;
        }
        b.setScaleX(1.0);
        b.setScaleY(1.0);
        b.getStyleClass().removeAll("selected");
    }

    // keyboard: Enter and Escape
    public void enableKeyboard(Scene scene) {
        // ensure button visuals reflect focus/keyboard (if you want to auto-start pulse when scene opens)
        scene.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                // simulate pressing the back button (and visual)
                startPulse(backButton);
                backButton.fire();
                ev.consume();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                controller.backToMenu();
                ev.consume();
            }
        });

        // when focus changes, keep visuals consistent
        scene.focusOwnerProperty().addListener((obs, oldV, newV) -> {
            // if focus is the button, ensure selected style/pulse
            if (newV == backButton) {
                startPulse(backButton);
                if (leftHand != null) { leftHand.setVisible(true); rightHand.setVisible(true); }
            } else {
                stopPulse(backButton);
                if (leftHand != null) { leftHand.setVisible(false); rightHand.setVisible(false); }
            }
        });
    }

    public Node getRoot() {
        return rootStack;
    }
}
