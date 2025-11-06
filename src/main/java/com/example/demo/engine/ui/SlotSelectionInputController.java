package com.example.demo.engine.ui;

import com.example.demo.controller.view.SlotSelectionController;
import com.example.demo.model.menu.ButtonManager;
import com.example.demo.model.menu.SaveSlot;
import com.example.demo.view.ui.SlotSelectionView;
import javafx.scene.input.KeyCode;

import java.util.List;


public class SlotSelectionInputController {

    private final SlotSelectionController controller;
    private final ButtonManager buttonManager;
    private final SlotSelectionView view;

    private int selectedSlotIndex = 0;
    private int selectedButtonIndex = -1;

    public SlotSelectionInputController( SlotSelectionView view, SlotSelectionController controller, ButtonManager buttonManager) {
        this.view = view;
        this.controller = controller;
        this.buttonManager = buttonManager;
    }

    public void handleInput(KeyCode key) {
        if (selectedButtonIndex == -1) {
            handleSlotNavigation(key);
        } else {
            handleButtonNavigation(key);
        }
    }

    private void handleSlotNavigation(KeyCode key) {
        List<SaveSlot> slots = controller.getAllSlots();
        switch (key) {
            case UP -> {
                selectedSlotIndex = (selectedSlotIndex - 1 + slots.size()) % slots.size();
                updateVisual();
            }
            case DOWN -> {
                selectedSlotIndex = (selectedSlotIndex + 1) % slots.size();
                updateVisual();
            }
            case ENTER -> {
                selectedButtonIndex = 0;
                updateVisual();
            }
            case ESCAPE -> controller.handleBackToMenu();
        }
    }

    private void handleButtonNavigation(KeyCode key) {
        int buttonCount = view.getButtonCountInSlot(selectedSlotIndex);
        switch (key) {
            case LEFT -> {
                if (buttonCount > 1) {
                    selectedButtonIndex = (selectedButtonIndex - 1 + buttonCount) % buttonCount;
                    updateVisual();
                }
            }
            case RIGHT -> {
                if (buttonCount > 1) {
                    selectedButtonIndex = (selectedButtonIndex + 1) % buttonCount;
                    updateVisual();
                }
            }
            case ENTER -> executeSelectedButton();
            case ESCAPE -> {
                selectedButtonIndex = -1;
                updateVisual();
            }
        }
    }

    private void updateVisual() {
        if (selectedButtonIndex == -1) {
            // chỉ chọn theo slot => không button nào được highlight
            buttonManager.setSelectedIndex(-1);
        } else {
            int globalIndex = view.getGlobalButtonIndex(selectedSlotIndex, selectedButtonIndex);
            buttonManager.setSelectedIndex(globalIndex);
        }
    }

    private void executeSelectedButton() {
        int globalIndex = view.getGlobalButtonIndex(selectedSlotIndex, selectedButtonIndex);
        if (globalIndex >= 0 && globalIndex < buttonManager.getButtons().size()) {
            buttonManager.getButtons().get(globalIndex).fire();
        }
    }

    public void reset() {
        selectedSlotIndex = 0;
        selectedButtonIndex = -1;
        updateVisual();
    }
}
