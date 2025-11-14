package com.example.demo.model.menu;

public class Achievement {
    private final String name;
    private final boolean unlocked;


    public Achievement(String name, boolean unlocked) {
        this.name = name;
        this.unlocked = unlocked;
    }

    public String getName() { return name; }
    public boolean isUnlocked() { return unlocked; }
}
