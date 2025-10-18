package com.example.demo.view;

import com.example.demo.controller.ButtonManager;
import com.example.demo.controller.MenuControll;
import com.example.demo.controller.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;


public class MenuView {
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

    private void buildUI() {
        Text title = new Text("My Awesome Game");
        title.setFont(Font.font(34));
        title.setFill(Color.WHITE);

        VBox menuBox = new VBox(28);
        menuBox.setAlignment(Pos.CENTER);

        // Tạo buttons - code ngắn gọn hơn rất nhiều!
        menuBox.getChildren().add(
                buttonManager.createButtonRow("Play", e -> controller.isPlaying())
        );
        menuBox.getChildren().add(
                buttonManager.createButtonRow("Settings", e -> controller.isSettings())
        );
        menuBox.getChildren().add(
                buttonManager.createButtonRow("Exit", e -> controller.isExit())
        );

        uiBox.getChildren().addAll(title, menuBox);
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
