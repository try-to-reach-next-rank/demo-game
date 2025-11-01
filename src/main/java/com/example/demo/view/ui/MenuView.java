package com.example.demo.view.ui;

import com.example.demo.controller.view.ButtonController;
import com.example.demo.controller.map.MenuController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.Stage;
import com.example.demo.model.core.effects.GlowTextEffect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Text;


public class MenuView implements Stage {
    private final MenuController controller;
    private final ThemeController themeManager;
    private final ButtonController buttonManager;
    private static StackPane rootStack = null;
    private final VBox uiBox;

    public MenuView(MenuController controller) {
        this.controller = controller;
        this.rootStack = new StackPane();
        this.uiBox = new VBox(18);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        // 1. Setup theme
        this.themeManager = new ThemeController();
        themeManager.setupBackground(rootStack);
        themeManager.applyCss(rootStack);

        // 2. Setup buttons
        this.buttonManager = new ButtonController(themeManager.getHandImage());
        buildUI();

        // 3. Stack layout
        rootStack.getChildren().add(uiBox);
        StackPane.setAlignment(uiBox, Pos.CENTER);
    }

    @Override
    public void buildUI() {
        GlowTextEffect glowTitle = new GlowTextEffect();
        glowTitle.activate(0, 0, 100000); // start shimmering

        Text titleNode = glowTitle.getNode(); // this is the one that glows

        VBox menuBox = new VBox(28);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(
                buttonManager.createButtonRow("Play", e -> controller.isSelecting()),
                buttonManager.createButtonRow("Settings", e -> controller.isSettings()),
                buttonManager.createButtonRow("Exit", e -> controller.isExit())
        );

        uiBox.getChildren().addAll(titleNode, menuBox);
    }

    public void enableKeyboard(Scene scene) {
        // Thêm sound callback nếu muốn
        buttonManager.setOnSelectionChanged(() -> {
            // TODO: Sound.playMenuMove();
        });

        buttonManager.enableKeyboardNavigation(scene);
    }

    public static Node getRoot() {
        return rootStack;
    }

    public void stopBgAnimation() {
        themeManager.stopBgAnimation();
    }
}
