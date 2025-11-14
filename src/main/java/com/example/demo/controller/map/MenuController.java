package com.example.demo.controller.map;

import com.example.demo.model.menu.MenuModel;
import javafx.application.Platform;

public record MenuController(MenuModel model) {

    public void isSelecting() {
        model.setCurrentScreen(MenuModel.Screen.SELECT);
    }

    public void isSettings() {
        model.setCurrentScreen(MenuModel.Screen.SETTINGS);
    }

    public void isAchievement() {
        model.setCurrentScreen(MenuModel.Screen.ACHIEVEMENT);
    }

    public void isExit() {
        model.setCurrentScreen(MenuModel.Screen.EXIT);
        Platform.exit();
    }
}