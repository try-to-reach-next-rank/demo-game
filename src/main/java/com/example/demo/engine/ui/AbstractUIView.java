package com.example.demo.engine.ui;

import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.Stage;
import com.example.demo.engine.input.InputHandler;
import com.example.demo.model.menu.ButtonManager;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;


public abstract class AbstractUIView implements Stage, InputHandler, NavigableUI {
    protected final ThemeController themeManager;
    protected final StackPane root;

    protected AbstractUIView(ThemeController themeManager) {
        this.themeManager = themeManager;
        this.root = new StackPane();
        setupTheme();
    }

    private void setupTheme() {
        themeManager.setupBackground(root);
        themeManager.applyCss(root);
    }

    @Override
    public void enableKeyboard(Scene scene) {
        scene.setOnKeyPressed(e -> handleInput(e.getCode()));
    }

    public StackPane getRoot() {
        return root;
    }

    public void stopBgAnimation() {
        themeManager.stopBgAnimation();
    }
}
