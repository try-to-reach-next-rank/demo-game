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

import com.example.demo.utils.var.AssetPaths;
import com.example.demo.utils.var.GameVar;

/**
 * ThemeManager quản lý background (animated hoặc gradient) và resources chung
 */
public class ThemeController {
    private static final List<Image> bgFrames = new ArrayList<>();
    private static final ImageView bgView = new ImageView();
    private static Timeline bgTimeline;
    private static int bgFrameIndex = 0;
    private static Duration bgFrameDuration = Duration.millis(140);

    private Image handImage = null;

    public ThemeController() {
        loadHandImage(AssetPaths.HAND);
        loadBgFrames("/images/bg/frame_", GameVar.DEFAULT_BG_FRAMES);
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
    public static void setupBackground(StackPane rootStack) {
        if (!bgFrames.isEmpty()) {
            setupAnimatedBackground(rootStack);
        } else {
            setupGradientBackground(rootStack);
        }
    }

    private static void setupAnimatedBackground(StackPane rootStack) {
        bgView.setPreserveRatio(false);
        bgView.setImage(bgFrames.get(0));
        bgView.fitWidthProperty().bind(rootStack.widthProperty());
        bgView.fitHeightProperty().bind(rootStack.heightProperty());

        rootStack.getChildren().add(0, bgView);
        StackPane.setAlignment(bgView, Pos.CENTER);

        startBgAnimation();
    }

    private static void setupGradientBackground(StackPane rootStack) {
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

    private static void startBgAnimation() {
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
    public static void applyCss(Region node) {
        try {
            String css = ThemeController.class.getResource(AssetPaths.CSS_PATH_MENU).toExternalForm();
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
}