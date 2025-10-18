package com.example.demo.model.map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MenuModel {
    public enum Screen { MENU, SELECT, PLAY, PAUSE, SETTINGS, GUIDE, EXIT}

    private final ObjectProperty<Screen> currentScreen = new SimpleObjectProperty<>(Screen.MENU);

    public ObjectProperty<Screen> currentScreenProperty() { return currentScreen; }
    public Screen getCurrentScreen() { return currentScreen.get(); }
    public void setCurrentScreen(Screen screen) { currentScreen.set(screen); }
}