package com.example.demo.view;

import com.example.demo.controller.ButtonManager;
import com.example.demo.controller.MenuControll;
import com.example.demo.controller.Stage;
import com.example.demo.controller.ThemeManager;
import com.example.demo.model.core.effects.GlowTextEffect;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;


public class MenuView implements Stage {
    private final MenuControll controller;
    private final ThemeManager themeManager;
    private final ButtonManager buttonManager;
    private static StackPane rootStack = null;
    private final VBox uiBox;

    public MenuView(MenuControll controller) {
        this.controller = controller;
        this.rootStack = new StackPane();
        this.uiBox = new VBox(18);
        this.uiBox.setPadding(new Insets(28));
        this.uiBox.setAlignment(Pos.CENTER);

        // 1. Setup theme
        this.themeManager = new ThemeManager();
        themeManager.setupBackground(rootStack);
        themeManager.applyCss(rootStack);

        // 2. Setup buttons
        this.buttonManager = new ButtonManager(themeManager.getHandImage());
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
