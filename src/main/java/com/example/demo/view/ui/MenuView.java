package com.example.demo.view.ui;

import com.example.demo.controller.map.MenuController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.ui.AbstractUIView;
import com.example.demo.engine.ui.UISelectionController;
import com.example.demo.model.core.effects.GlowTextEffect;
import com.example.demo.model.menu.ButtonManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

public class MenuView extends AbstractUIView {
    private final MenuController controller;
    private final ButtonManager buttonManager;
    private final VBox uiBox;
    private final UISelectionController selectionController;

    public MenuView(MenuController controller, ThemeController themeManager, ButtonManager buttonManager) {
        super(themeManager);
        this.controller = controller;
        this.buttonManager = buttonManager;

        this.uiBox = new VBox(18);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        buildUI();

        List<String> options = Arrays.asList("Play", "Settings", "Exit");
        this.selectionController = new UISelectionController(options);
        selectionController.setOnConfirm(this::handleConfirm);

        root.getChildren().add(uiBox);
        StackPane.setAlignment(uiBox, Pos.CENTER);
    }

    @Override
    public void buildUI() {
        GlowTextEffect glowTitle = new GlowTextEffect();
        glowTitle.activate(0, 0, 100000);
        Text titleNode = glowTitle.getNode();

        VBox menuBox = new VBox(28);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(
                buttonManager.createButtonRow("Play", e -> controller.isSelecting()),
                buttonManager.createButtonRow("Settings", e -> controller.isSettings()),
                buttonManager.createButtonRow("Exit", e -> controller.isExit())
        );

        uiBox.getChildren().addAll(titleNode, menuBox);
    }

    // -----------------------------
    // InputHandler
    // -----------------------------
    @Override
    public void handleInput(KeyCode code) {
        switch (code) {
            case UP -> moveUp();
            case DOWN -> moveDown();
            case ENTER -> confirm();
            case ESCAPE -> cancel();
        }
    }

    // -----------------------------
    // NavigableUI
    // -----------------------------
    @Override
    public void moveUp() {
        selectionController.moveUp();
    }

    @Override
    public void moveDown() {
        selectionController.moveDown();
    }

    @Override
    public void moveLeft() {}
    @Override
    public void moveRight() {}

    @Override
    public void confirm() {
        selectionController.confirm();
    }

    @Override
    public void cancel() {
        // Quay lại nếu có stack stage
    }

    private void handleConfirm(String option) {
        switch (option) {
            case "Play" -> controller.isSelecting();
            case "Settings" -> controller.isSettings();
            case "Exit" -> controller.isExit();
        }
    }
}
