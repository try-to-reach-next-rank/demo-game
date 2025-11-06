package com.example.demo.view.ui;

import com.example.demo.controller.view.SlotSelectionController;
import com.example.demo.engine.ui.SlotSelectionInputController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.ui.AbstractUIView;
import com.example.demo.model.menu.ButtonManager;
import com.example.demo.model.menu.SaveSlot;
import com.example.demo.view.SlotComponent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * View cho Slot Selection Menu.
 * Chỉ chịu trách nhiệm hiển thị và liên kết dữ liệu với ButtonManager.
 * Input và điều hướng được xử lý bởi SlotSelectionInputController.
 */
public class SlotSelectionView extends AbstractUIView {

    private final SlotSelectionController controller;
    private final SlotSelectionInputController inputController;
    private final ButtonManager buttonManager;

    private final VBox uiBox;
    private VBox slotsBox;

    private final List<SlotComponent> slotComponents = new ArrayList<>();

    public SlotSelectionView( SlotSelectionController controller, ThemeController themeManager, ButtonManager buttonManager) {
        super(themeManager);
        this.controller = controller;
        this.buttonManager = new ButtonManager(themeManager.getHandImage());
        // input controller riêng biệt
        this.inputController = new SlotSelectionInputController(this, controller, buttonManager);

        this.uiBox = new VBox(30);
        this.uiBox.setPadding(new Insets(40));
        this.uiBox.setAlignment(Pos.CENTER);

        root.getStylesheets().add(
                getClass().getResource("/styles/slot.css").toExternalForm()
        );

        buildUI();
        root.getChildren().add(uiBox);
        StackPane.setAlignment(uiBox, Pos.CENTER);
    }

    // ============================================================
    // UI BUILDING
    // ============================================================
    @Override
    public void buildUI() {
        Text title = new Text("Select Save Slot");
        title.setFont(Font.font(38));
        title.setFill(Color.WHITE);

        slotsBox = new VBox(40);
        slotsBox.setAlignment(Pos.CENTER);

        List<SaveSlot> slots = controller.getAllSlots();
        for (SaveSlot slot : slots) {
            SlotComponent slotComp = new SlotComponent(slot);
            slotComponents.add(slotComp);
            slotsBox.getChildren().add(slotComp);
            setupSlotButtons(slotComp);
        }

        uiBox.getChildren().addAll(title, slotsBox);
    }

    private void setupSlotButtons(SlotComponent slotComp) {
        SaveSlot slot = slotComp.getSlot();
        HBox buttonBox = slotComp.getButtonBox();

        if (slot.isEmpty()) {
            HBox newGameBtn = buttonManager.createButtonRow(
                    "New Game",
                    e -> controller.handleNewGame(slot.getSlotNumber())
            );
            buttonBox.getChildren().add(newGameBtn);
        } else {
            HBox playBtn = buttonManager.createButtonRow(
                    "Play",
                    e -> controller.handleContinueGame(slot.getSlotNumber())
            );
            HBox deleteBtn = buttonManager.createButtonRow(
                    "Delete",
                    e -> {
                        controller.handleDeleteSlot(slot.getSlotNumber());
                        refreshSlots();
                    }
            );
            buttonBox.getChildren().addAll(playBtn, deleteBtn);
        }
    }

    // ============================================================
    // INPUT
    // ============================================================
    @Override
    public void enableKeyboard(Scene scene) {
        scene.setOnKeyPressed(ev -> {
            inputController.handleInput(ev.getCode());
            ev.consume();
        });
    }

    // ============================================================
    // DATA + REFRESH
    // ============================================================
    public void refreshSlots() {
        controller.refreshSlots();
        List<SaveSlot> slots = controller.getAllSlots();

        slotsBox.getChildren().clear();
        slotComponents.clear();
        buttonManager.clearAll();

        for (SaveSlot slot : slots) {
            SlotComponent comp = new SlotComponent(slot);
            slotComponents.add(comp);
            slotsBox.getChildren().add(comp);
            setupSlotButtons(comp);
        }

        inputController.reset();
    }

    // ============================================================
    // SUPPORT ACCESSORS
    // ============================================================
    public List<SaveSlot> getSlots() {
        return controller.getAllSlots();
    }

    public int getGlobalButtonIndex(int slotIdx, int localBtnIdx) {
        int globalIdx = 0;
        List<SaveSlot> slots = controller.getAllSlots();
        for (int i = 0; i < slotIdx; i++) {
            SaveSlot slot = slots.get(i);
            globalIdx += slot.isEmpty() ? 1 : 2;
        }
        return globalIdx + localBtnIdx;
    }

    public int getButtonCountInSlot(int slotIdx) {
        List<SaveSlot> slots = controller.getAllSlots();
        if (slotIdx < 0 || slotIdx >= slots.size()) return 0;
        SaveSlot slot = slots.get(slotIdx);
        return slot.isEmpty() ? 1 : 2;
    }

    public List<SlotComponent> getSlotComponents() {
        return slotComponents;
    }

    // ============================================================
    // ABSTRACTUIView METHODS (NOT USED)
    // ============================================================
    @Override public void handleInput(javafx.scene.input.KeyCode code) { }
    @Override public void moveUp() { }
    @Override public void moveDown() { }
    @Override public void moveLeft() { }
    @Override public void moveRight() { }
    @Override public void confirm() { }
    @Override public void cancel() { }
}
