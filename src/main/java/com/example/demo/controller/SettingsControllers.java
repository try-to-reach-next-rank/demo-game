package com.example.demo.controller;

import com.example.demo.model.map.MenuModel;
import com.example.demo.model.map.SettingsModel;

public class SettingsControllers {
    private final SettingsModel model;
    private final MenuModel menuModel;

    public SettingsControllers(SettingsModel model, MenuModel menuModel) {
        this.model = model;
        this.menuModel = menuModel;
    }

    public void backToMenu() {
        menuModel.setCurrentScreen(MenuModel.Screen.MENU);
    }

    public SettingsModel getModel() {
        return model;
    }
}