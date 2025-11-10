package com.example.demo.model.core.entities;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.BrickData;
import com.example.demo.view.graphics.BrickTextureProvider;

/**
 * Pure data class representing a brick in the game.
 * Contains only state and basic getters/setters.
 */
public class Brick extends ImageObject<BrickData> {
    private int health;
    private boolean destroyed;

    public Brick(String imageKey, double x, double y, int health) {
        super(imageKey, x, y);
        this.health = health;
        this.destroyed = false;
    }

    @Override
    public void applyState(BrickData data) {
        if (data == null) return;
        this.setHealth(data.getHealth());
        this.setDestroyed(data.isDestroyed());

        // Nếu gạch được "hồi sinh", cập nhật lại texture
        if (!this.isDestroyed()) {
            String newImageKey = BrickTextureProvider.getTextureForHealth(this.getHealth());
            this.setImageKey(newImageKey);
        }
    }

    public boolean takeDamage(int damage) {
        if (health == Integer.MAX_VALUE) return false;
        health -= damage;
        if (health <= 0) {
            destroyed = true;
            return true;
        }

        setImageKey(BrickTextureProvider.getTextureForHealth(health));
        return false;
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }

    // For CoreView
    @Override
    public boolean isVisible() {
        return super.isVisible() && !isDestroyed(); 
    }
}
