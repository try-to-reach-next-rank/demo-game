package com.example.demo.controller.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * ThemeManager quản lý background (animated hoặc gradient) và resources chung
 */
public class ThemeManager {
    private final List<Image> bgFrames = new ArrayList<>();
    private final ImageView bgView = new ImageView();
    private Timeline bgTimeline;
    private int bgFrameIndex = 0;
    private Duration bgFrameDuration = Duration.millis(140);

    private Image handImage = null;
    private String cssPath = "/styles/menu.css";

    private static final int DEFAULT_BG_FRAMES = 6;

    public ThemeManager() {
        loadHandImage("/images/hand.png");
        loadBgFrames("/images/bg/frame_", DEFAULT_BG_FRAMES);
    }

    // -------------------------
    // Resource Loading
    // -------------------------

    private void loadHandImage(String resourcePath) {
        try {
            var url = getClass().getResource(resourcePath);
            if (url != null) {
                handImage = new Image(url.toExternalForm(), true);
            } else {
                System.err.println("[ThemeManager] hand image not found at " + resourcePath);
            }
        } catch (Exception ex) {
            System.err.println("[ThemeManager] error loading hand image: " + ex.getMessage());
        }
    }

    private void loadBgFrames(String basePath, int count) {
        bgFrames.clear();
        for (int i = 0; i < count; i++) {
            String path = basePath + i + ".png";
            try {
                var url = getClass().getResource(path);
                if (url != null) {
                    bgFrames.add(new Image(url.toExternalForm(), true));
                } else {
                    System.err.println("[ThemeManager] bg frame missing: " + path);
                }
            } catch (Exception ex) {
                System.err.println("[ThemeManager] error loading frame " + path + ": " + ex.getMessage());
            }
        }
    }

    // -------------------------
    // Background Setup
    // -------------------------

    /**
     * Setup background cho StackPane. Tự động chọn animated nếu có frames,
     * hoặc fallback sang gradient.
     */
    public void setupBackground(StackPane rootStack) {
        if (!bgFrames.isEmpty()) {
            setupAnimatedBackground(rootStack);
        } else {
            setupGradientBackground(rootStack);
        }
    }

    private void setupAnimatedBackground(StackPane rootStack) {
        bgView.setPreserveRatio(false);
        bgView.setImage(bgFrames.get(0));

        // Bind size to container
        bgView.fitWidthProperty().bind(rootStack.widthProperty());
        bgView.fitHeightProperty().bind(rootStack.heightProperty());

        rootStack.getChildren().add(0, bgView);
        StackPane.setAlignment(bgView, Pos.CENTER);

        startBgAnimation();
    }

    private void setupGradientBackground(StackPane rootStack) {
        Region bgRegion = new Region();
        BackgroundFill fill = new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#000000")),
                        new Stop(1, Color.web("#000000"))),
                CornerRadii.EMPTY,
                Insets.EMPTY
        );
        bgRegion.setBackground(new Background(fill));

        bgRegion.prefWidthProperty().bind(rootStack.widthProperty());
        bgRegion.prefHeightProperty().bind(rootStack.heightProperty());

        rootStack.getChildren().add(0, bgRegion);
        StackPane.setAlignment(bgRegion, Pos.CENTER);
    }

    // -------------------------
    // Background Animation
    // -------------------------

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
        if (bgTimeline != null) {
            bgTimeline.stop();
        }
    }

    // -------------------------
    // CSS Management
    // -------------------------

    /**
     * Apply CSS stylesheet cho node
     */
    public void applyCss(Region node) {
        try {
            String css = getClass().getResource(cssPath).toExternalForm();
            node.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("[ThemeManager] Failed to load CSS: " + e.getMessage());
        }
    }

    // -------------------------
    // Getters & Setters
    // -------------------------

    public Image getHandImage() {
        return handImage;
    }

    public void setBgFrameDuration(Duration duration) {
        if (duration != null && !duration.equals(bgFrameDuration)) {
            bgFrameDuration = duration;
            if (bgTimeline != null && bgTimeline.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                startBgAnimation();
            }
        }
    }

    public void registerBgFrames(List<Image> frames) {
        bgFrames.clear();
        if (frames != null) {
            bgFrames.addAll(frames);
        }
        if (!bgFrames.isEmpty()) {
            startBgAnimation();
        }
    }

    public void setCssPath(String path) {
        this.cssPath = path;
    }
}