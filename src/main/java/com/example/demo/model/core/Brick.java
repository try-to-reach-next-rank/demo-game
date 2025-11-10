package com.example.demo.model.core;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.BrickData;
import com.example.demo.model.state.gameobjectdata.GameObjectData;
import com.example.demo.model.state.gameobjectdata.ImageObjectData;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.graphics.BrickTextureProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pure data class representing a brick in the game.
 * Contains only state and basic getters/setters.
 */

public class Brick extends ImageObject<BrickData> {
    private int scoreValue;
    private int health;
    private final int initialHealth;
    private boolean destroyed;

    public Brick(String imageKey, double x, double y, int health) {
        super(imageKey, x, y);
        this.health = health;
        this.initialHealth = health;
        this.destroyed = false;
        this.imageKey = imageKey;
        this.scoreValue = calculateScore(initialHealth);
    }

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
    
    private int calculateScore(int initialHealth) {
        if (initialHealth == Integer.MAX_VALUE) return 0; // gạch bất tử
        if (initialHealth <= 0) return 0;
        // cách tính điểm: 1->10,2->20,...5->50 (score = health * 10)
        return initialHealth * 10;
    }

    public int getInitialHealth() {
        return initialHealth;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }

    public boolean isStatic() {
        // TODO: Upgrade this later
        return false;
    }


}
