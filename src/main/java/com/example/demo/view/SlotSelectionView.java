package com.example.demo.view;

import com.example.demo.controller.SlotSelectionController;
import com.example.demo.controller.ThemeManager;
import com.example.demo.model.menu.SaveSlot;
import com.example.demo.view.SlotComponent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;



/**
 * View cho Slot Selection Menu
 * Hiển thị 2 save slots với mini map preview
 */
public class SlotSelectionView {
    private final SlotSelectionController controller;
    private final ThemeManager themeManager;

    private final StackPane rootStack;
    private final VBox uiBox;

    // Slot components
    private List<SlotComponent> slotComponents;
    private int selectedSlotIndex = 0;  // 0 = slot 1, 1 = slot 2
    private int selectedButtonIndex = -1;  // -1 = chọn slot, 0/1 = chọn button

    public SlotSelectionView(SlotSelectionController controller) {
        this.controller = controller;
        this.themeManager = new ThemeManager();
        this.rootStack = new StackPane();
        this.uiBox = new VBox(30);
        this.slotComponents = new ArrayList<>();

        buildUI();
    }

    /**
     * Setup toàn bộ UI
     */

    private void buildUI() {
        // Setup background
        ThemeManager.setupBackground(rootStack);
        ThemeManager.applyCss(rootStack);

        // Setup UI container
        uiBox.setAlignment(Pos.CENTER);
        uiBox.setPadding(new Insets(40));

        // Title
        Text title = new Text("Select Save Slot");
        title.setFont(Font.font(38));
        title.setFill(Color.WHITE);

        // Slots container (horizontal layout)
        HBox slotsBox = new HBox(40);
        slotsBox.setAlignment(Pos.CENTER);

        // Load slots từ controller
        List<SaveSlot> slots = controller.getAllSlots();

        for (SaveSlot slot : slots) {
            SlotComponent component = new SlotComponent(slot);
            slotComponents.add(component);
            slotsBox.getChildren().add(component);

            // Attach button handlers
            attachButtonHandlers(component);
        }

        uiBox.getChildren().addAll(title, slotsBox);
        rootStack.getChildren().add(uiBox);

        // Initial selection visual
        updateSelectionVisuals();
    }

    /**
     * Attach event handlers cho buttons của component
     */
    private void attachButtonHandlers(SlotComponent component) {
        SaveSlot slot = component.getSlot();

        if (slot.isEmpty()) {
            // Empty slot: New Game button
            if (component.getNewGameButton() != null) {
                component.getNewGameButton().setOnAction(e -> {
                    controller.handleNewGame(slot.getSlotNumber());
                });
            }
        } else {
            // Occupied slot: Play và Delete buttons
            if (component.getPlayButton() != null) {
                component.getPlayButton().setOnAction(e -> {
                    controller.handleContinueGame(slot.getSlotNumber());
                });
            }

            if (component.getDeleteButton() != null) {
                component.getDeleteButton().setOnAction(e -> {
                    controller.handleDeleteSlot(slot.getSlotNumber());
                });
            }
        }
    }

    /**
     * Update visual feedback cho selection
     */
    private void updateSelectionVisuals() {
        // Reset all slots
        for (int i = 0; i < slotComponents.size(); i++) {
            SlotComponent component = slotComponents.get(i);

            if (i == selectedSlotIndex) {
                // Selected slot
                component.setStyle(component.getStyle() +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-width: 3;" +
                        "-fx-effect: dropshadow(gaussian, #00ff88, 15, 0.7, 0, 0);"
                );

                // Highlight buttons nếu đang chọn button
                if (selectedButtonIndex >= 0) {
                    highlightButton(component, selectedButtonIndex);
                }
            } else {
                // Non-selected slot
                component.setStyle(component.getStyle() +
                        "-fx-border-color: #2a6cff;" +
                        "-fx-border-width: 2;"
                );
            }
        }
    }

    /**
     * Highlight button trong selected slot
     */
    private void highlightButton(SlotComponent component, int buttonIndex) {
        SaveSlot slot = component.getSlot();

        if (slot.isEmpty()) {
            // Chỉ có 1 button: New Game
            if (component.getNewGameButton() != null) {
                component.getNewGameButton().getStyleClass().add("selected");
            }
        } else {
            // 2 buttons: Play và Delete
            if (buttonIndex == 0 && component.getPlayButton() != null) {
                component.getPlayButton().getStyleClass().add("selected");
                component.getDeleteButton().getStyleClass().remove("selected");
            } else if (buttonIndex == 1 && component.getDeleteButton() != null) {
                component.getDeleteButton().getStyleClass().add("selected");
                component.getPlayButton().getStyleClass().remove("selected");
            }
        }
    }

    /**
     * Enable keyboard navigation
     */
    public void enableKeyboard(Scene scene) {
        scene.setOnKeyPressed(ev -> {
            KeyCode key = ev.getCode();

            if (selectedButtonIndex == -1) {
                // Mode: Selecting slot
                handleSlotNavigation(key);
            } else {
                // Mode: Selecting button
                handleButtonNavigation(key);
            }

            ev.consume();
        });
    }

    /**
     * Handle keyboard navigation giữa slots
     */
    private void handleSlotNavigation(KeyCode key) {
        switch (key) {
            case LEFT:
                selectedSlotIndex = (selectedSlotIndex - 1 + slotComponents.size())
                        % slotComponents.size();
                updateSelectionVisuals();
                break;

            case RIGHT:
                selectedSlotIndex = (selectedSlotIndex + 1) % slotComponents.size();
                updateSelectionVisuals();
                break;

            case ENTER:
                // Enter slot → switch to button selection mode
                selectedButtonIndex = 0;
                updateSelectionVisuals();
                break;

            case ESCAPE:
                // Back to main menu
                controller.handleBackToMenu();
                break;
        }
    }

    /**
     * Handle keyboard navigation giữa buttons
     */
    private void handleButtonNavigation(KeyCode key) {
        SlotComponent selectedComponent = slotComponents.get(selectedSlotIndex);
        SaveSlot selectedSlot = selectedComponent.getSlot();

        switch (key) {
            case LEFT:
                if (!selectedSlot.isEmpty()) {
                    selectedButtonIndex = 0;  // Play button
                    updateSelectionVisuals();
                }
                break;

            case RIGHT:
                if (!selectedSlot.isEmpty()) {
                    selectedButtonIndex = 1;  // Delete button
                    updateSelectionVisuals();
                }
                break;

            case ENTER:
                // Execute button action
                executeSelectedButton(selectedComponent);
                break;

            case ESCAPE:
                // Back to slot selection mode
                selectedButtonIndex = -1;
                updateSelectionVisuals();
                break;
        }
    }

    /**
     * Execute action của button đang được select
     */
    private void executeSelectedButton(SlotComponent component) {
        SaveSlot slot = component.getSlot();

        if (slot.isEmpty()) {
            // New Game
            controller.handleNewGame(slot.getSlotNumber());
        } else {
            // Play hoặc Delete
            if (selectedButtonIndex == 0) {
                controller.handleContinueGame(slot.getSlotNumber());
            } else if (selectedButtonIndex == 1) {
                controller.handleDeleteSlot(slot.getSlotNumber());
            }
        }
    }

    /**
     * Refresh slots sau khi delete/create
     */
    public void refreshSlots() {
        // Reload slots từ controller
        List<SaveSlot> slots = controller.getAllSlots();

        // Clear old components
        HBox slotsBox = (HBox) uiBox.getChildren().get(1);
        slotsBox.getChildren().clear();
        slotComponents.clear();

        // Recreate components
        for (SaveSlot slot : slots) {
            SlotComponent component = new SlotComponent(slot);
            slotComponents.add(component);
            slotsBox.getChildren().add(component);
            attachButtonHandlers(component);
        }

        // Reset selection
        selectedButtonIndex = -1;
        updateSelectionVisuals();
    }

    /**
     * Lấy root node cho Scene
     */
    public Node getRoot() {
        return rootStack;
    }

    /**
     * Stop background animation
     */
    public void stopBgAnimation() {
        themeManager.stopBgAnimation();
    }
}



