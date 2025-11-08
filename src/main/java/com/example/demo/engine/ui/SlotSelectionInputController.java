package com.example.demo.engine.ui;

import com.example.demo.controller.view.SlotSelectionController;
import com.example.demo.model.menu.ButtonManager;
import com.example.demo.model.menu.SaveSlot;
import com.example.demo.view.ui.SlotSelectionView;

import java.util.List;


public class SlotSelectionInputController {

    private final SlotSelectionController controller;
    private final ButtonManager buttonManager;
    private final SlotSelectionView view;

    private int selectedSlotIndex = 0;
    private int selectedButtonIndex = -1; // -1: Chỉ chọn Slot, >= 0: Chọn Button trong Slot

    // Constructor được giữ nguyên
    public SlotSelectionInputController( SlotSelectionView view, SlotSelectionController controller, ButtonManager buttonManager) {
        this.view = view;
        this.controller = controller;
        this.buttonManager = buttonManager;
    }


    // ============================================================
    // NAVIGATION LOGIC (Tận dụng NavigableUI verbs)
    // ============================================================

    public void moveUp() {
        List<SaveSlot> slots = controller.getAllSlots();
        selectedSlotIndex = (selectedSlotIndex - 1 + slots.size()) % slots.size();
        selectedButtonIndex = -1; // Trở về chế độ chọn Slot khi di chuyển UP/DOWN
        updateVisual();
    }

    public void moveDown() {
        List<SaveSlot> slots = controller.getAllSlots();
        selectedSlotIndex = (selectedSlotIndex + 1) % slots.size();
        selectedButtonIndex = -1;
        updateVisual();
    }

    public void moveLeft() {
        int buttonCount = view.getButtonCountInSlot(selectedSlotIndex);

        if (selectedButtonIndex == -1) {
            // Đang chọn Slot, chuyển sang chọn Button cuối cùng nếu có
            if (buttonCount > 0) {
                selectedButtonIndex = buttonCount - 1;
            }
        } else {
            // Đang chọn Button, di chuyển giữa các Button
            if (buttonCount > 1) {
                selectedButtonIndex = (selectedButtonIndex - 1 + buttonCount) % buttonCount;
            }
        }
        updateVisual();
    }

    public void moveRight() {
        int buttonCount = view.getButtonCountInSlot(selectedSlotIndex);

        if (selectedButtonIndex == -1) {
            // Đang chọn Slot, chuyển sang chọn Button đầu tiên nếu có
            if (buttonCount > 0) {
                selectedButtonIndex = 0;
            }
        } else {
            // Đang chọn Button, di chuyển giữa các Button
            if (buttonCount > 1) {
                selectedButtonIndex = (selectedButtonIndex + 1) % buttonCount;
            }
        }
        updateVisual();
    }

    public void confirm() {
        if (selectedButtonIndex != -1) {
            executeSelectedButton();
        } else {
            // Đang chọn Slot: Kích hoạt nút mặc định (thường là nút đầu tiên)
            int buttonCount = view.getButtonCountInSlot(selectedSlotIndex);
            if (buttonCount > 0) {
                // Tạm thời chọn nút đầu tiên và thực thi
                selectedButtonIndex = 0;
                executeSelectedButton();
                selectedButtonIndex = -1; // Quay lại chế độ chọn Slot sau khi thực thi
            }
        }
    }

    public void cancel() {
        if (selectedButtonIndex != -1) {
            // Hủy chọn nút, trở lại chế độ chọn Slot
            selectedButtonIndex = -1;
            updateVisual();
        } else {
            // Hủy khi chỉ chọn Slot: Quay về Menu
            controller.handleBackToMenu();
        }
    }

    // những method update visual và thực thi lệnh

    private void updateVisual() {
        if (selectedButtonIndex == -1) {
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