package com.example.demo.view;

import com.example.demo.controller.MenuControll;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class MenuView {
    private final MenuControll controller;
    private static StackPane rootStack = null;
    private final VBox uiBox;

    private final List<Button> buttons = new ArrayList<>();
    private final List<ImageView> leftHands = new ArrayList<>();
    private final List<ImageView> rightHands = new ArrayList<>();

    private int selectedIndex = 0;

    // background animation
    private final List<Image> bgFrames = new ArrayList<>();
    private final ImageView bgView = new ImageView();
    private Timeline bgTimeline;
    private int bgFrameIndex = 0;
    private Duration bgFrameDuration = Duration.millis(140);

    // hand image
    private Image handImage = null;
    private static final int DEFAULT_BG_FRAMES = 6;

    public MenuView(MenuControll controller) {
        this.controller = controller;
        this.rootStack = new StackPane();
        this.uiBox = new VBox(18);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        // load resources
        loadHandImage("/images/hand.png");
        loadBgFrames("/images/bg/frame_", DEFAULT_BG_FRAMES);

        // setup background and ui
        setupBackground();
        buildUI();

        rootStack.getChildren().addAll(bgView, uiBox);
        StackPane.setAlignment(bgView, Pos.CENTER);
        StackPane.setAlignment(uiBox, Pos.CENTER);

        bgView.fitWidthProperty().bind(rootStack.widthProperty());
        bgView.fitHeightProperty().bind(rootStack.heightProperty());

        updateSelectionVisuals();
    }

    // -------------------------
    // Resource loading
    // -------------------------
    private void loadHandImage(String resourcePath) {
        try {
            var url = getClass().getResource(resourcePath);
            if (url != null) {
                handImage = new Image(url.toExternalForm(), true);
            } else {
                System.err.println("[MenuView] hand image not found at " + resourcePath);
            }
        } catch (Exception ex) {
            System.err.println("[MenuView] error loading hand image: " + ex.getMessage());
        }
    }

    private void loadBgFrames(String basePath, int count) {
        bgFrames.clear();
        for (int i = 0; i < count; ++i) {
            String path = basePath + i + ".png";
            try {
                var url = getClass().getResource(path);
                if (url != null) {
                    bgFrames.add(new Image(url.toExternalForm(), true));
                } else {
                    System.err.println("[MenuView] bg frame missing: " + path);
                }
            } catch (Exception ex) {
                System.err.println("[MenuView] error loading frame " + path + " : " + ex.getMessage());
            }
        }
    }

    // -------------------------
    // Background setup & animation
    // -------------------------
    private void setupBackground() {
        if (!bgFrames.isEmpty()) {
            bgView.setPreserveRatio(false);
            bgView.setImage(bgFrames.get(0));
            startBgAnimation();
        } else {
            Region bgRegion = new Region();
            BackgroundFill fill = new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true,
                            CycleMethod.NO_CYCLE,
                            new Stop(0, Color.web("#0b3a62")),
                            new Stop(1, Color.web("#04263a"))),
                    CornerRadii.EMPTY, Insets.EMPTY);
            bgRegion.setBackground(new Background(fill));
            bgRegion.prefWidthProperty().bind(rootStack.widthProperty());
            bgRegion.prefHeightProperty().bind(rootStack.heightProperty());
            bgView.setImage(null);
            rootStack.getChildren().add(0, bgRegion);
            StackPane.setAlignment(bgRegion, Pos.CENTER);
        }
    }

    private void startBgAnimation() {
        if (bgFrames.isEmpty()) return;
        if (bgTimeline != null) bgTimeline.stop();
        bgFrameIndex = 0;
        bgView.setImage(bgFrames.get(0));
        bgTimeline = new Timeline(new KeyFrame(bgFrameDuration, e -> {
            bgFrameIndex = (bgFrameIndex + 1) % bgFrames.size();
            bgView.setImage(bgFrames.get(bgFrameIndex));
        }));
        bgTimeline.setCycleCount(Timeline.INDEFINITE);
        bgTimeline.play();
    }

    public void stopBgAnimation() {
        if (bgTimeline != null) bgTimeline.stop();
    }

    // -------------------------
    // UI build
    // -------------------------
    private void buildUI() {
        Text title = new Text("My Awesome Game");
        title.setFont(Font.font(34));
        title.setFill(Color.WHITE);

        VBox menuBox = new VBox(28);
        menuBox.setAlignment(Pos.CENTER);

        addMenuRow("Play", e -> controller.isPlaying(), menuBox);
        addMenuRow("Settings", e -> controller.isSettings(), menuBox);
        addMenuRow("Exit", e -> controller.isExit(), menuBox);

        uiBox.getChildren().addAll(title, menuBox);
    }

    private void addMenuRow(String label, EventHandler<ActionEvent> handler, VBox parent) {
        ImageView left = new ImageView();
        ImageView right = new ImageView();
        if (handImage != null) {
            left.setImage(handImage);
            right.setImage(handImage);
            right.setScaleX(-1);
        }
        left.setFitWidth(28);
        left.setFitHeight(28);
        left.setPreserveRatio(true);
        left.setVisible(false);

        right.setFitWidth(28);
        right.setFitHeight(28);
        right.setPreserveRatio(true);
        right.setVisible(false);

        Button b = new Button(label);
        b.setMinWidth(220);
        b.setFocusTraversable(false);
        b.setFont(Font.font(18));
        b.setOnAction(handler);

        // --- unify hover + keyboard ---
        b.setOnMouseEntered(e -> {
            int idx = buttons.indexOf(b);
            if (idx >= 0) {
                selectedIndex = idx;
                updateSelectionVisuals();
            }
        });

        b.setStyle("""
            -fx-background-color: linear-gradient(#6aa0ff, #2a6cff);
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-scale-x: 1;
            -fx-scale-y: 1;
        """);

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(left, b, right);

        buttons.add(b);
        leftHands.add(left);
        rightHands.add(right);
        parent.getChildren().add(row);
    }

    // -------------------------
    // Unified selection visuals
    // -------------------------
    private void updateSelectionVisuals() {
        for (int i = 0; i < buttons.size(); ++i) {
            Button btn = buttons.get(i);
            ImageView l = leftHands.get(i);
            ImageView r = rightHands.get(i);
            boolean isSelected = (i == selectedIndex);

            l.setVisible(isSelected);
            r.setVisible(isSelected);

            if (isSelected) {
                btn.setStyle("""
                    -fx-background-color: linear-gradient(#6aa0ff, #2a6cff);
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-scale-x: 1.06;
                    -fx-scale-y: 1.06;
                """);
            } else {
                btn.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: white;
                    -fx-font-weight: normal;
                    -fx-scale-x: 1;
                    -fx-scale-y: 1;
                """);
            }
        }
    }

    // -------------------------
    // Keyboard navigation
    // -------------------------
    public void enableKeyboard(Scene scene) {
        updateSelectionVisuals();

        scene.setOnKeyPressed(ev -> {
            KeyCode k = ev.getCode();
            if (k == KeyCode.UP) {
                selectedIndex = (selectedIndex - 1 + buttons.size()) % buttons.size();
                updateSelectionVisuals();
                ev.consume();
            } else if (k == KeyCode.DOWN) {
                selectedIndex = (selectedIndex + 1) % buttons.size();
                updateSelectionVisuals();
                ev.consume();
            } else if (k == KeyCode.ENTER || k == KeyCode.SPACE) {
                buttons.get(selectedIndex).fire();
                ev.consume();
            }
        });
    }

    // -------------------------
    // Public API
    // -------------------------
    public static Node getRoot() {
        return rootStack;
    }

    public void setBgFrameDuration(Duration d) {
        if (d != null) {
            bgFrameDuration = d;
            if (bgTimeline != null) startBgAnimation();
        }
    }

    public void registerBgFrames(List<Image> frames) {
        bgFrames.clear();
        if (frames != null) bgFrames.addAll(frames);
        if (!bgFrames.isEmpty()) startBgAnimation();
    }
}
