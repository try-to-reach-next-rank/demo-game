package com.example.demo.view;

import com.example.demo.controller.MenuControll;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

/**
 * MenuView — final revised:
 * - Loads /images/hand.png as hand icon; uses one Image for left and flipped for right.
 * - Loads background frames from /images/bg/frame_0.png ... frame_N.png and animates them.
 * - If no frames, uses a gradient Region background.
 * - Supports mouse hover -> selection, keyboard (UP/DOWN/ENTER/SPACE).
 * - Shows two hands at the selected row and pulses the selected button.
 *
 * Place resources:
 *  - hand: src/main/resources/images/hand.png
 *  - bg frames: src/main/resources/images/bg/frame_0.png ... frame_n.png (optional)
 *
 * Controller (MenuControll) must expose: onPlay(), onGuide(), onSettings(), onExit()
 */
public class MenuView {
    private final MenuControll controller;
    private final StackPane rootStack;
    private final VBox uiBox;

    private final List<Button> buttons = new ArrayList<>();
    private final List<ImageView> leftHands = new ArrayList<>();
    private final List<ImageView> rightHands = new ArrayList<>();
    private final Map<Button, ScaleTransition> activePulses = new HashMap<>();

    private int selectedIndex = 0;

    // background animation
    private final List<Image> bgFrames = new ArrayList<>();
    private final ImageView bgView = new ImageView();
    private Timeline bgTimeline;
    private int bgFrameIndex = 0;
    private Duration bgFrameDuration = Duration.millis(140);

    // single hand image
    private Image handImage = null;

    private static final int DEFAULT_BG_FRAMES = 6;

    public MenuView(MenuControll controller) {
        this.controller = controller;
        this.rootStack = new StackPane();
        this.uiBox = new VBox(18);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        // load resources (hand + bg frames)
        loadHandImage("/images/hand.png");
        loadBgFrames("/images/bg/frame_", DEFAULT_BG_FRAMES);

        // setup background and ui
        setupBackground();
        buildUI();

        // stack order: background view (bgView/gradient region) at index 0, UI on top
        rootStack.getChildren().addAll(bgView, uiBox);
        StackPane.setAlignment(bgView, Pos.CENTER);
        StackPane.setAlignment(uiBox, Pos.CENTER);

        // bind bgView size to root size so it always fills the Scene
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
            handImage = null;
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
                    // missing frame is OK — just report
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
            if (!bgFrames.isEmpty()) bgView.setImage(bgFrames.get(0));
            startBgAnimation();
        } else {
            // fallback gradient background: place a Region behind ui
            Region bgRegion = new Region();
            BackgroundFill fill = new BackgroundFill(
                    new javafx.scene.paint.LinearGradient(0, 0, 0, 1, true,
                            javafx.scene.paint.CycleMethod.NO_CYCLE,
                            new javafx.scene.paint.Stop(0, Color.web("#0b3a62")),
                            new javafx.scene.paint.Stop(1, Color.web("#04263a"))),
                    CornerRadii.EMPTY, Insets.EMPTY);
            bgRegion.setBackground(new Background(fill));
            // bind size to stack
            bgRegion.prefWidthProperty().bind(rootStack.widthProperty());
            bgRegion.prefHeightProperty().bind(rootStack.heightProperty());
            // place region behind bgView (so we need to add region into stack and hide bgView)
            bgView.setImage(null);
            // put bgRegion as first child
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

        VBox menuBox = new VBox(12);
        menuBox.setAlignment(Pos.CENTER);

        // add rows; controller methods called on action
        addMenuRow("Play", e -> controller.isPlaying(), menuBox);
        addMenuRow("Guide", e -> controller.isGuide(), menuBox);
        addMenuRow("Settings", e -> controller.isSettings(), menuBox);
        addMenuRow("Exit", e -> controller.isExit(), menuBox);

        uiBox.getChildren().addAll(title, menuBox);

        // layout tweaks: ensure buttons widths match and hands align
        // compute max button width after layout pass
        uiBox.widthProperty().addListener((obs, oldV, newV) -> {
            // nothing special here, button sizes are explicit (minWidth)
        });
    }

    private void addMenuRow(String label, javafx.event.EventHandler<javafx.event.ActionEvent> handler, VBox parent) {
        ImageView left = new ImageView();
        ImageView right = new ImageView();
        if (handImage != null) {
            left.setImage(handImage);
            right.setImage(handImage);
            right.setScaleX(-1); // flip horizontally for right-hand
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

        // mouse hover selects this row
        b.setOnMouseEntered(e -> {
            int idx = buttons.indexOf(b);
            if (idx >= 0) {
                selectedIndex = idx;
                updateSelectionVisuals();
            }
        });

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(left, b, right);

        buttons.add(b);
        leftHands.add(left);
        rightHands.add(right);
        parent.getChildren().add(row);
    }

    // -------------------------
    // Selection visuals & pulse
    // -------------------------
    private void updateSelectionVisuals() {
        for (int i = 0; i < buttons.size(); ++i) {
            Button btn = buttons.get(i);
            ImageView l = leftHands.get(i);
            ImageView r = rightHands.get(i);
            if (i == selectedIndex) {
                l.setVisible(true);
                r.setVisible(true);
                btn.setStyle("-fx-background-color: linear-gradient(#6aa0ff, #2a6cff); -fx-text-fill: white; -fx-font-weight: bold;");
                startPulse(btn);
            } else {
                l.setVisible(false);
                r.setVisible(false);
                btn.setStyle("");
                stopPulse(btn);
            }
        }
    }

    private void startPulse(Button b) {
        // stop pulses for others
        for (Button other : new ArrayList<>(activePulses.keySet())) {
            if (other != b) stopPulse(other);
        }
        if (activePulses.containsKey(b)) return;
        ScaleTransition st = new ScaleTransition(Duration.millis(420), b);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.06);
        st.setToY(1.06);
        st.setCycleCount(Timeline.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
        activePulses.put(b, st);
    }

    private void stopPulse(Button b) {
        ScaleTransition st = activePulses.remove(b);
        if (st != null) {
            st.stop();
            b.setScaleX(1.0);
            b.setScaleY(1.0);
        }
    }

    // -------------------------
    // Keyboard navigation API
    // -------------------------
    public void enableKeyboard(Scene scene) {
        // ensure initial visuals
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

        // keep visuals updated when focus changes
        scene.focusOwnerProperty().addListener((obs, oldV, newV) -> updateSelectionVisuals());
    }

    // -------------------------
    // Public API
    // -------------------------
    public Node getRoot() {
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
