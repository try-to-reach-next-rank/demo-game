package com.example.demo.model.map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.example.demo.model.utils.Sound;

public class SettingsModel {

    public SettingsModel() {
        // Kết nối với Sound manager
        Sound.getInstance().bindSettings(this);
    }

    // Audio settings
    private final DoubleProperty musicVolume = new SimpleDoubleProperty(1.0);
    private final DoubleProperty effectVolume = new SimpleDoubleProperty(1.0);
    private final BooleanProperty musicEnabled = new SimpleBooleanProperty(true);
    private final BooleanProperty effectEnabled = new SimpleBooleanProperty(true);

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