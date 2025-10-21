package com.example.demo.model.state;

public class ActivePowerUpData {
    private String type;
    private long remainingDuration;

    public ActivePowerUpData() {}

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getRemainingDuration() {
        return remainingDuration;
    }

    public void setRemainingDuration(long remainingDuration) {
        this.remainingDuration = remainingDuration;
    }
}
