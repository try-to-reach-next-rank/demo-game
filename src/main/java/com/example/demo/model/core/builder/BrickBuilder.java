package com.example.demo.model.core.builder;

import com.example.demo.model.core.entities.Brick;

public class BrickBuilder {
    private String imageKey;

    // Optional properties with defaults
    private double x = 0;
    private double y = 0;
    private int health = 1;
    private boolean destroyed = false;

    public BrickBuilder() {} // default constructor

    public BrickBuilder imageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }

    public BrickBuilder position(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public BrickBuilder health(int health) {
        this.health = health;
        return this;
    }

    public BrickBuilder destroyed(boolean destroyed) {
        this.destroyed = destroyed;
        return this;
    }

    public Brick build() {
        if (imageKey == null || imageKey.isEmpty()) {
            throw new IllegalStateException("Brick imageKey must be set");
        }

        Brick brick = new Brick(imageKey, x, y, health);
        brick.setDestroyed(destroyed);
        return brick;
    }
}
