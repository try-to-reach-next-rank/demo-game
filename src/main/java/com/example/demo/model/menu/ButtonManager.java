package com.example.demo.model.menu;

import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ButtonManager quản lý buttons với hand animations, hover effects,
 * keyboard navigation và pulse animations
 */
public class ButtonManager {
    private final List<Button> buttons = new ArrayList<>();
    private final List<ImageView> leftHands = new ArrayList<>();
    private final List<ImageView> rightHands = new ArrayList<>();
    private final Map<Button, ScaleTransition> activePulses = new HashMap<>();

    private int selectedIndex = 0;
    private int lastSelectedIndex = -1;

    private final Image handImage;
    private Runnable onSelectionChanged; // callback khi selection thay đổi (để play sound)

    public ButtonManager(Image handImage) {
        this.handImage = handImage;
    }

    // -------------------------
    // Button Creation
    // -------------------------

    /**
     * Tạo một button row với hands và animations
     * @return HBox chứa [leftHand, button, rightHand]
     */
    public HBox createButtonRow(String label, EventHandler<ActionEvent> handler) {
        ImageView left = new ImageView();
        ImageView right = new ImageView();

        if (handImage != null) {
            left.setImage(handImage);
            right.setImage(handImage);
            right.setScaleX(-1); // flip horizontally
        }

        left.setFitWidth(28);
        left.setFitHeight(28);
        left.setPreserveRatio(true);
        left.setVisible(false);

        right.setFitWidth(28);
        right.setFitHeight(28);
        right.setPreserveRatio(true);
        right.setVisible(false);

        Button button = new Button(label);
        button.setMinWidth(220);
        button.setFocusTraversable(false);
        button.setFont(Font.font(18));
        button.setOnAction(handler);

        // Hover animations
        addHoverAnimation(button);

        // Mouse interactions để update selection
        int buttonIndex = buttons.size();
        button.setOnMouseEntered(e -> {
            selectedIndex = buttonIndex;
            updateSelectionVisuals();
        });

        button.setOnMouseExited(e -> {
            hideHands(buttonIndex);
            stopPulse(button);
            button.getStyleClass().remove("selected");
        });

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(left, button, right);

        buttons.add(button);
        leftHands.add(left);
        rightHands.add(right);

        return row;
    }

    public HBox createButtonRowPosition(String label, EventHandler<ActionEvent> handler, float x, float y) {
        ImageView left = new ImageView();
        ImageView right = new ImageView();

        if (handImage != null) {
            left.setImage(handImage);
            right.setImage(handImage);
            right.setScaleX(-1); // flip horizontally
        }

        left.setFitWidth(28);
        left.setFitHeight(28);
        left.setPreserveRatio(true);
        left.setVisible(false);

        right.setFitWidth(28);
        right.setFitHeight(28);
        right.setPreserveRatio(true);
        right.setVisible(false);

        Button button = new Button(label);
        button.setMinWidth(220);
        button.setFocusTraversable(false);
        button.setFont(Font.font(18));
        button.setOnAction(handler);

        // Hover animations
        addHoverAnimation(button);

        // Mouse interactions để update selection
        int buttonIndex = buttons.size();
        button.setOnMouseEntered(e -> {
            selectedIndex = buttonIndex;
            updateSelectionVisuals();
        });

        button.setOnMouseExited(e -> {
            hideHands(buttonIndex);
            stopPulse(button);
            button.getStyleClass().remove("selected");
        });

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(left, button, right);

        buttons.add(button);
        leftHands.add(left);
        rightHands.add(right);

        row.setLayoutX(x);
        row.setLayoutY(y);

        return row;
    }






    // -------------------------
    // Animations
    // -------------------------

    private void addHoverAnimation(Button button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(180), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(180), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private void startPulse(Button button) {
        // Stop other pulses
        for (Button other : new ArrayList<>(activePulses.keySet())) {
            if (other != button) stopPulse(other);
        }

        if (activePulses.containsKey(button)) return;

        ScaleTransition st = new ScaleTransition(Duration.millis(420), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.06);
        st.setToY(1.06);
        st.setCycleCount(Timeline.INDEFINITE);
        st.setAutoReverse(true);
        st.play();

        activePulses.put(button, st);
    }

    private void stopPulse(Button button) {
        ScaleTransition st = activePulses.remove(button);
        if (st != null) {
            st.stop();
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        }
    }

    // -------------------------
    // Selection Management
    // -------------------------

    private void updateSelectionVisuals() {
        if (buttons.isEmpty()) return;

        // Remove highlight from previous
        if (lastSelectedIndex >= 0 && lastSelectedIndex < buttons.size()) {
            Button prevBtn = buttons.get(lastSelectedIndex);
            prevBtn.getStyleClass().remove("selected");
            leftHands.get(lastSelectedIndex).setVisible(false);
            rightHands.get(lastSelectedIndex).setVisible(false);
            stopPulse(prevBtn);
        }

        // Add highlight to current
        if (selectedIndex >= 0 && selectedIndex < buttons.size()) {
            Button currBtn = buttons.get(selectedIndex);
            if (!currBtn.getStyleClass().contains("selected")) {
                currBtn.getStyleClass().add("selected");
            }
            leftHands.get(selectedIndex).setVisible(true);
            rightHands.get(selectedIndex).setVisible(true);
            startPulse(currBtn);
        }

        lastSelectedIndex = selectedIndex;
    }

    private void hideHands(int index) {
        if (index >= 0 && index < buttons.size()) {
            leftHands.get(index).setVisible(false);
            rightHands.get(index).setVisible(false);
        }
    }

    public void setSelectedIndex(int index) {
        if (index < 0) {
            if (lastSelectedIndex >= 0 && lastSelectedIndex < buttons.size()) {
                Button prev = buttons.get(lastSelectedIndex);
                prev.getStyleClass().remove("selected");
                leftHands.get(lastSelectedIndex).setVisible(false);
                rightHands.get(lastSelectedIndex).setVisible(false);
                stopPulse(prev);
            }

            lastSelectedIndex = -1;
            selectedIndex = -1;
            return;
        }
        if (index >= 0 && index < buttons.size()) {
            selectedIndex = index;
            updateSelectionVisuals();
        }
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void clearAll() {
        buttons.clear();
        leftHands.clear();
        rightHands.clear();
        activePulses.clear();
        selectedIndex = 0;
        lastSelectedIndex = -1;
    }


    public void navigateUp() {
        if (buttons.isEmpty()) return;
        selectedIndex = (selectedIndex - 1 + buttons.size()) % buttons.size();
        updateSelectionVisuals();
        if (onSelectionChanged != null) onSelectionChanged.run();
    }

    public void navigateDown() {
        if (buttons.isEmpty()) return;
        selectedIndex = (selectedIndex + 1) % buttons.size();
        updateSelectionVisuals();
        if (onSelectionChanged != null) onSelectionChanged.run();
    }

    public void activateSelected() {
        if (selectedIndex >= 0 && selectedIndex < buttons.size()) {
            buttons.get(selectedIndex).fire();
        }
    }
}

