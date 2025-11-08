package com.example.demo.controller.view;

import com.example.demo.model.menu.SettingsModel;


public class SettingsController {
    private final SettingsModel model;
    private Runnable onBackToMenu;

    public SettingsController(SettingsModel model) {
        this.model = model;
    }

    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void backToMenu() {
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }

    public SettingsModel getModel() {
        return model;
    }
}