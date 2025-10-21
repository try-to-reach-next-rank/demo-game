package com.example.demo.model.state;

import com.example.demo.model.core.bricks.Brick;

public class BrickData {
    private int id;
    private int health;
    private boolean isDestroyed;

    public BrickData() {}

    public BrickData(int id, Brick brick) {
        this.id = id;
        this.health = brick.getHealth();
        this.isDestroyed = brick.isDestroyed();
    }

    public int getId() { return id; }
    public int getHealth() { return health; }
    public boolean isDestroyed() { return isDestroyed; }
}