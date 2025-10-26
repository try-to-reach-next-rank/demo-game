package com.example.demo.view;

import com.example.demo.controller.SlotSelectionController;
import com.example.demo.controller.Stage;
import com.example.demo.controller.ThemeManager;
import com.example.demo.controller.ButtonManager;
import com.example.demo.model.menu.SaveSlot;
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
 * Mỗi slot là button lớn, chứa mini map và action buttons bên trong
 * Mouse: click trực tiếp vào action buttons
 * Keyboard: chọn slot (LEFT/RIGHT) → ENTER để vào button mode → chọn button (LEFT/RIGHT) → ENTER để execute
 */
public class SlotSelectionView implements Stage {
    private final SlotSelectionController controller;
    private final ThemeManager themeManager;
    private final ButtonManager buttonManager;

    private static StackPane rootStack = null;
    private final VBox uiBox;
    private VBox slotsBox;

    // Navigation state
    private List<SlotComponent> slotComponents;
    private int selectedSlotIndex = 0;
    private int selectedButtonIndex = -1; // -1 = selecting slot, 0+ = selecting button inside slot

    public SlotSelectionView(SlotSelectionController controller) {
        this.controller = controller;
        this.rootStack = new StackPane();
        this.uiBox = new VBox(30);
        this.uiBox.setPadding(new Insets(40));
        this.uiBox.setAlignment(Pos.CENTER);
        this.slotComponents = new ArrayList<>();
        this.rootStack.getStylesheets().add(
                getClass().getResource("/styles/slot.css").toExternalForm()
        );

        // 1. Setup theme
        this.themeManager = new ThemeManager();
        ThemeManager.setupBackground(rootStack);
        ThemeManager.applyCss(rootStack);

        // 2. Setup buttons
        this.buttonManager = new ButtonManager(themeManager.getHandImage());
        buildUI();

        // 3. Stack layout
        rootStack.getChildren().add(uiBox);
        StackPane.setAlignment(uiBox, Pos.CENTER);
    }

    @Override
    public void buildUI() {
        Text title = new Text("Select Save Slot");
        title.setFont(Font.font(38));
        title.setFill(Color.WHITE);

        slotsBox = new VBox(40);
        slotsBox.setAlignment(Pos.CENTER);

        // Load slots
        List<SaveSlot> slots = controller.getAllSlots();
        for (SaveSlot slot : slots) {
            SlotComponent slotComp = new SlotComponent(slot);
            slotComponents.add(slotComp);
            slotsBox.getChildren().add(slotComp);

            setupSlotButtons(slotComp);

            int slotIndex = slotComponents.size() - 1;
            slotComp.setOnAction(e -> {
                selectedSlotIndex = slotIndex;
                selectedButtonIndex = -1;
                updateSelectionVisuals();
            });
        }

        uiBox.getChildren().addAll(title, slotsBox);
        updateSelectionVisuals();
    }

    /**
     * Setup action buttons bên trong slot
     */
    private void setupSlotButtons(SlotComponent slotComp) {
        SaveSlot slot = slotComp.getSlot();
        HBox buttonBox = slotComp.getButtonBox();

        if (slot.isEmpty()) {
            // Empty slot: New Game
            HBox btnRow = buttonManager.createButtonRow("New Game", e ->
                    controller.handleNewGame(slot.getSlotNumber())
            );
            btnRow.setOnMouseClicked(e -> e.consume());
            buttonBox.getChildren().add(btnRow);
        } else {
            // Occupied slot: Play + Delete
            HBox playRow = buttonManager.createButtonRow("Play", e ->
                    controller.handleContinueGame(slot.getSlotNumber())
            );
            playRow.setOnMouseClicked(e -> e.consume());

            HBox deleteRow = buttonManager.createButtonRow("Delete", e -> {
                controller.handleDeleteSlot(slot.getSlotNumber());
                refreshSlots();
            });
            deleteRow.setOnMouseClicked(e -> e.consume());

            buttonBox.getChildren().addAll(playRow, deleteRow);
        }
    }

    /**
     * Update visual feedback
     */
    private void updateSelectionVisuals() {
        // Update slot highlight
        for (int i = 0; i < slotComponents.size(); i++) {
            slotComponents.get(i).setSelected(i == selectedSlotIndex);
        }

        // Update button highlight
        if (selectedButtonIndex >= 0 && selectedSlotIndex >= 0) {
            int globalIndex = getGlobalButtonIndex(selectedSlotIndex, selectedButtonIndex);
            buttonManager.setSelectedIndex(globalIndex);
        } else {
            // Không chọn button nào → clear toàn bộ hiệu ứng
            buttonManager.setSelectedIndex(-1);
        }
    }

    /**
     * Tính global button index từ slot và local button index
     */
    private int getGlobalButtonIndex(int slotIdx, int localBtnIdx) {
        int globalIdx = 0;
        for (int i = 0; i < slotIdx; i++) {
            SaveSlot slot = slotComponents.get(i).getSlot();
            globalIdx += slot.isEmpty() ? 1 : 2;
        }
        return globalIdx + localBtnIdx;
    }

    /**
     * Lấy số buttons trong slot hiện tại
     */
    private int getButtonCountInSlot(int slotIdx) {
        if (slotIdx < 0 || slotIdx >= slotComponents.size()) return 0;
        SaveSlot slot = slotComponents.get(slotIdx).getSlot();
        return slot.isEmpty() ? 1 : 2;
    }

    /**
     * Enable keyboard navigation
     */
    public void enableKeyboard(Scene scene) {
        scene.setOnKeyPressed(ev -> {
            KeyCode key = ev.getCode();
            if (selectedButtonIndex == -1)
                handleSlotNavigation(key);
            else
                handleButtonNavigation(key);
            ev.consume();
        });
    }

    /**
     *Handle keyboard navigation giữa slots
     */
    private void handleSlotNavigation(KeyCode key) {
        switch (key) {
            case DOWN:
                selectedSlotIndex = (selectedSlotIndex - 1 + slotComponents.size()) % slotComponents.size();
                updateSelectionVisuals();
                break;
            case UP:
                selectedSlotIndex = (selectedSlotIndex + 1) % slotComponents.size();
                updateSelectionVisuals();
                break;
            case ENTER:
                selectedButtonIndex = 0;
                updateSelectionVisuals();
                break;
            case ESCAPE:
                controller.handleBackToMenu();
                break;
        }
    }

    /**
     * Handle keyboard navigation giữa buttons bên trong slot
     */

    private void handleButtonNavigation(KeyCode key) {
        int count = getButtonCountInSlot(selectedSlotIndex);
        switch (key) {
            case LEFT:
                if (count > 1) {
                    selectedButtonIndex = (selectedButtonIndex - 1 + count) % count;
                    updateSelectionVisuals();
                }
                break;
            case RIGHT:
                if (count > 1) {
                    selectedButtonIndex = (selectedButtonIndex + 1) % count;
                    updateSelectionVisuals();
                }
                break;
            case ENTER:
                executeSelectedButton();
                break;
            case ESCAPE:
                selectedButtonIndex = -1;
                updateSelectionVisuals();
                break;
        }
    }

    /**
     * Execute action của button đang được select
     */
    private void executeSelectedButton() {
        int globalIndex = getGlobalButtonIndex(selectedSlotIndex, selectedButtonIndex);
        if (globalIndex >= 0 && globalIndex < buttonManager.getButtons().size())
            buttonManager.getButtons().get(globalIndex).fire();
    }

    /**
     * Refresh slots sau khi delete/create
     */
    public void refreshSlots() {
        List<SaveSlot> slots = controller.getAllSlots();
        slotsBox.getChildren().clear();
        slotComponents.clear();
        buttonManager.clearAll();

        for (SaveSlot slot : slots) {
            SlotComponent comp = new SlotComponent(slot);
            slotComponents.add(comp);
            slotsBox.getChildren().add(comp);
            setupSlotButtons(comp);

            int slotIndex = slotComponents.size() - 1;
            comp.setOnAction(e -> {
                selectedSlotIndex = slotIndex;
                selectedButtonIndex = -1;
                updateSelectionVisuals();
            });
        }

        selectedSlotIndex = 0;
        selectedButtonIndex = -1;
        updateSelectionVisuals();
    }

    public static Node getRoot() {
        return rootStack;
    }

    public void stopBgAnimation() {
        themeManager.stopBgAnimation();
    }
}
