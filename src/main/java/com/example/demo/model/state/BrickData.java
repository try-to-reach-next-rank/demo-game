package com.example.demo.model.state;

import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.state.gameobjectdata.ImageObjectData;

public class BrickData extends ImageObjectData {
    private int id;
    private int health;
    private int initialHealth;
    private boolean isDestroyed;
    private String type;      // NORMAL, STEEL, etc.
    private String imageKey;  // to restore visual state

    public BrickData(int id, Brick brick, String type) {
        super(brick);
        this.id = id;
        this.health = brick.getHealth();
        this.initialHealth = brick.getInitialHealth();
        this.isDestroyed = brick.isDestroyed();
        this.type = type;
        this.imageKey = brick.getImageKey();
    }

    public int getId() { return id; }
    public int getHealth() { return health; }
    public int getInitialHealth() { return initialHealth; }
    public boolean isDestroyed() { return isDestroyed; }
    public String getType() { return type; }
    public String getImageKey() { return imageKey; }
}