package com.example.demo.model.state;

public class SettingsState {
    private double musicVolume;
    private double effectVolume;
    private boolean musicEnabled;
    private boolean effectEnabled;

    public SettingsState() {}

    // --- Getters and Setters ---

    public double getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(double musicVolume) {
        this.musicVolume = musicVolume;
    }

    public double getEffectVolume() {
        return effectVolume;
    }

    public void setEffectVolume(double effectVolume) {
        this.effectVolume = effectVolume;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isEffectEnabled() {
        return effectEnabled;
    }

    public void setEffectEnabled(boolean effectEnabled) {
        this.effectEnabled = effectEnabled;
    }
}