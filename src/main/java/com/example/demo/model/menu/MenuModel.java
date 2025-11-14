package com.example.demo.model.menu;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MenuModel {
    public enum Screen { MENU, SELECT, PLAY,  SETTINGS, EXIT, ACHIEVEMENT }

    private final ObjectProperty<Screen> currentScreen = new SimpleObjectProperty<>(Screen.MENU);

    public ObjectProperty<Screen> currentScreenProperty() { return currentScreen; }
    public Screen getCurrentScreen() { return currentScreen.get(); }
    public void setCurrentScreen(Screen s) { currentScreen.set(s); }
}