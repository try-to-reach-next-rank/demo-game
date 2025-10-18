package com.example.demo.controller;

import com.example.demo.model.map.MenuModel;
import javafx.application.Platform;

public class MenuControll {

    private final MenuModel model;

    public MenuControll(MenuModel model) {
        this.model = model;
    }

    public void isPlaying(){
        model.setCurrentScreen(MenuModel.Screen.PLAY);
        System.out.println("playing");
    }
    public void isSelecting() {
        model.setCurrentScreen(MenuModel.Screen.SELECT);
        System.out.println("Select profile");
    }


    public void isPause() {
        model.setCurrentScreen(MenuModel.Screen.PAUSE);
        System.out.println("Just Pausing");
    }

    public void isSettings() {
        model.setCurrentScreen(MenuModel.Screen.SETTINGS);
        System.out.println("Just Settings");
    }

    public void isExit() {
        model.setCurrentScreen(MenuModel.Screen.EXIT);
        Platform.exit();
    }
}