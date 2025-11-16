package com.example.demo.model.menu;

public class Achievement {
    private String name;
    private boolean unlocked;

    public Achievement() {
    }

    public Achievement(String name, boolean unlocked) {
        this.name = name;
        this.unlocked = unlocked;
    }

    public String getName() { return name; }
    public boolean isUnlocked() { return unlocked; }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
