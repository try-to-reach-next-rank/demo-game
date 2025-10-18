package com.example.demo.controller;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static ThemeManager instance;

    private final List<Image> bgFrames = new ArrayList<>();
    private Timeline bgTimeline;
    private int bgFrameIndex = 0;
    private Duration bgFrameDuration = Duration.millis(140);

    private ThemeManager() {
        loadBgFrames("/images/bg/frame_", 6);
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    // -------------------------------
    // Background animation management
    // -------------------------------
    private void loadBgFrames(String basePath, int count) {
        bgFrames.clear();
        for (int i = 0; i < count; ++i) {
            String path = basePath + i + ".png";
            try {
                var url = getClass().getResource(path);
                if (url != null) {
                    bgFrames.add(new Image(url.toExternalForm(), true));
                } else {
                    System.err.println("[ThemeManager] Missing bg frame: " + path);
                }
            } catch (Exception ex) {
                System.err.println("[ThemeManager] Error loading " + path + ": " + ex.getMessage());
            }
        }
    }

    public void applyBackground(StackPane rootStack) {
        ImageView bgView = new ImageView();
        bgView.setPreserveRatio(false);
        bgView.fitWidthProperty().bind(rootStack.widthProperty());
        bgView.fitHeightProperty().bind(rootStack.heightProperty());
        rootStack.getChildren().add(0, bgView);

        if (!bgFrames.isEmpty()) {
            bgView.setImage(bgFrames.get(0));
            startBgAnimation(bgView);
        } else {
            Region bgRegion = new Region();
            BackgroundFill fill = new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.web("#0b3a62")),
                            new Stop(1, Color.web("#04263a"))),
                    CornerRadii.EMPTY, Insets.EMPTY);
            bgRegion.setBackground(new Background(fill));
            bgRegion.prefWidthProperty().bind(rootStack.widthProperty());
            bgRegion.prefHeightProperty().bind(rootStack.heightProperty());
            rootStack.getChildren().add(0, bgRegion);
        }
    }

    private void startBgAnimation(ImageView bgView) {
        if (bgFrames.isEmpty()) return;
        if (bgTimeline != null) bgTimeline.stop();

        bgFrameIndex = 0;
        bgTimeline = new Timeline(new KeyFrame(bgFrameDuration, e -> {
            bgFrameIndex = (bgFrameIndex + 1) % bgFrames.size();
            bgView.setImage(bgFrames.get(bgFrameIndex));
        }));
        bgTimeline.setCycleCount(Timeline.INDEFINITE);
        bgTimeline.play();
    }

    // -------------------------------
    // Keyboard navigation
    // -------------------------------
    public void enableKeyboard(Scene scene, List<Node> buttons, Runnable onEscape) {
        if (buttons == null || buttons.isEmpty()) return;

        final int[] selectedIndex = {0};
        final ScaleTransition[] pulse = {null};

        scene.setOnKeyPressed(ev -> {
            KeyCode key = ev.getCode();
            if (key == KeyCode.UP) {
                selectedIndex[0] = (selectedIndex[0] - 1 + buttons.size()) % buttons.size();
                playPulse(buttons.get(selectedIndex[0]), pulse);
                ev.consume();
            } else if (key == KeyCode.DOWN) {
                selectedIndex[0] = (selectedIndex[0] + 1) % buttons.size();
                playPulse(buttons.get(selectedIndex[0]), pulse);
                ev.consume();
            } else if (key == KeyCode.ENTER) {
                buttons.get(selectedIndex[0]).fireEvent(
                        new javafx.event.ActionEvent(buttons.get(selectedIndex[0]), null)
                );
                ev.consume();
            } else if (key == KeyCode.ESCAPE && onEscape != null) {
                onEscape.run();
                ev.consume();
            }
        });
    }

    private void playPulse(Node node, ScaleTransition[] pulse) {
        if (pulse[0] != null) {
            pulse[0].stop();
        }
        node.setScaleX(1.0);
        node.setScaleY(1.0);
        pulse[0] = new ScaleTransition(Duration.millis(420), node);
        pulse[0].setFromX(1.0);
        pulse[0].setFromY(1.0);
        pulse[0].setToX(1.06);
        pulse[0].setToY(1.06);
        pulse[0].setCycleCount(Timeline.INDEFINITE);
        pulse[0].setAutoReverse(true);
        pulse[0].play();
    }
}
