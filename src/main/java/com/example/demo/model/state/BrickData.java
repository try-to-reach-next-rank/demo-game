package com.example.demo.model.state;

import com.example.demo.model.core.Brick;
import com.example.demo.model.state.gameobjectdata.GameObjectData;
import com.example.demo.model.state.gameobjectdata.ImageObjectData;

public class BrickData extends ImageObjectData {
    private int id;
    private int health;
    private int initialHealth;
    private boolean isDestroyed;

    public BrickData(int id, Brick brick) {
        super(brick);
        this.id = id;
        this.health = brick.getHealth();
        this.isDestroyed = brick.isDestroyed();
        this.initialHealth = brick.getInitialHealth();
    }

    public int getId() { return id; }
    public int getHealth() { return health; }
    public boolean isDestroyed() { return isDestroyed; }
}