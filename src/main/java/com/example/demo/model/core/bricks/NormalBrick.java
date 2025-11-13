package com.example.demo.model.core.bricks;

import com.example.demo.view.graphics.BrickTextureProvider;

public class NormalBrick extends Brick {
    public NormalBrick(int health, String imageKey, double x, double y, double width, double height) {
        super(health, imageKey, x, y, width, height);
    }

    @Override
    public boolean takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            destroyed = true;
            return true;
        }
        imageKey = BrickTextureProvider.getTextureForHealth(health);
        return false;
    }

    @Override
    public int getScoreValue() {
        if (initialHealth <= 0 || initialHealth == Integer.MAX_VALUE) return 0;
        return initialHealth * 10;
    }
}
