package com.example.demo.model.menu;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.example.demo.model.state.SettingsState;
import com.example.demo.controller.SaveManager;
import com.example.demo.model.utils.Sound;

import static com.example.demo.model.utils.GlobalVar.SETTINGS_FILE_PATH;


public class SettingsModel {

    // Audio settings
    private final DoubleProperty musicVolume = new SimpleDoubleProperty(1.0);
    private final DoubleProperty effectVolume = new SimpleDoubleProperty(1.0);
    private final BooleanProperty musicEnabled = new SimpleBooleanProperty(true);
    private final BooleanProperty effectEnabled = new SimpleBooleanProperty(true);

    public SettingsModel() {
        // Kết nối với Sound manager
        Sound.getInstance().bindSettings(this);


        // Load settings as soon as the game starts
        loadSettings();

        // Add listeners to automatically save when any changes occur
        musicVolume.addListener((obs, oldVal, newVal) -> saveSettings());
        effectVolume.addListener((obs, oldVal, newVal) -> saveSettings());
        musicEnabled.addListener((obs, oldVal, newVal) -> saveSettings());
        effectEnabled.addListener((obs, oldVal, newVal) -> saveSettings());
    }

    public void saveSettings() {
        System.out.println("Saving settings...");
        SettingsState state = new SettingsState();

        // Get the current values from the properties
        state.setMusicVolume(getMusicVolume());
        state.setEffectVolume(getEffectVolume());
        state.setMusicEnabled(isMusicEnabled());
        state.setEffectEnabled(isEffectEnabled());

        // Use the SaveManager to write the state to a file
        SaveManager.save(state, SETTINGS_FILE_PATH);
    }

    public void loadSettings() {
        // Use the SaveManager to load the state from a file
        SettingsState state = SaveManager.load(SETTINGS_FILE_PATH, SettingsState.class);

        // If the file exists, apply the settings
        if (state != null) {
            System.out.println("Loading settings...");
            setMusicVolume(state.getMusicVolume());
            setEffectVolume(state.getEffectVolume());
            setMusicEnabled(state.isMusicEnabled());
            setEffectEnabled(state.isEffectEnabled());
        } else {
            System.out.println("No settings file found. Using default settings.");
            // Optional: You could save the default settings on the first run
            // saveSettings();
        }
    }

    // Music volume control
    public double getMusicVolume() { return musicVolume.get(); }
    public void setMusicVolume(double value) { musicVolume.set(value); }
    public DoubleProperty musicVolumeProperty() { return musicVolume; }

    // Effect volume control
    public double getEffectVolume() { return effectVolume.get(); }
    public void setEffectVolume(double value) { effectVolume.set(value); }
    public DoubleProperty effectVolumeProperty() { return effectVolume; }

    // Music enable/disable
    public boolean isMusicEnabled() { return musicEnabled.get(); }
    public void setMusicEnabled(boolean value) { musicEnabled.set(value); }
    public BooleanProperty musicEnabledProperty() { return musicEnabled; }

    // Sound effects enable/disable
    public boolean isEffectEnabled() { return effectEnabled.get(); }
    public void setEffectEnabled(boolean value) { effectEnabled.set(value); }
    public BooleanProperty effectEnabledProperty() { return effectEnabled; }
}