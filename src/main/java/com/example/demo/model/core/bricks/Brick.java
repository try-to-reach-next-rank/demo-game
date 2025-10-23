package com.example.demo.model.core.bricks;

import com.example.demo.model.core.GameObject;
import com.example.demo.model.state.BrickData;
import com.example.demo.view.graphics.BrickTextureProvider;
import javafx.scene.image.Image;

/**
 * Pure data class representing a brick in the game.
 * Contains only state and basic getters/setters.
 */
public class Brick extends GameObject {
    private int health;
    private boolean destroyed;


    public Brick(Image image, double x, double y, int health) {
        super(image, x, y);
        this.health = health;
        this.destroyed = false;
    }

    public void applyState(BrickData data) {
        if (data == null) return;
        this.setHealth(data.getHealth());
        this.setDestroyed(data.isDestroyed());
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }

}
