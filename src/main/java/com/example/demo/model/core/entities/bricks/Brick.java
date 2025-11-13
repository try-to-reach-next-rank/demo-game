package com.example.demo.model.core.entities.bricks;

import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.model.state.BrickData;
import javafx.scene.image.Image;

public abstract class Brick extends GameObject<BrickData> {
    protected int health;
    protected final int initialHealth;
    protected boolean destroyed;
    protected String imageKey;

    public Brick(int health, String imageKey, double x, double y, double width, double height) {
        super(x, y);
        this.health = health;
        this.initialHealth = health;
        this.destroyed = false;
        this.imageKey = imageKey;
        this.width = width;
        this.height = height;
        this.baseWidth = width;
        this.baseHeight = height;
    }

    public abstract boolean takeDamage(int damage);

    public int getHealth() { return health; }
    public boolean isDestroyed() { return destroyed; }
    public String getImageKey() { return imageKey; }
    public Image getImage() {return AssetManager.getInstance().getImage(imageKey);}

    @Override
    public void applyState(BrickData data) {
        if (data == null) return;
        this.health = data.getHealth();
        this.destroyed = data.isDestroyed();
        // Optionally update imageKey here
    }

    // For CoreView
    @Override
    public boolean isVisible() {
        return super.isVisible() && !isDestroyed(); 
    }

    public int getInitialHealth() {
        return health;
    }

    public abstract int getScoreValue();
}