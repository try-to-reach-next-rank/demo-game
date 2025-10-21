package com.example.demo.model.core;

import com.example.demo.controller.AssetManager;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.utils.Animation;
import com.example.demo.model.utils.GlobalVar;

public class PowerUp extends GameObject {
    private final String animKey;
    private final Animation anim;
    private final String type;
    private boolean visible;
    private final double fallSpeed = 150.0;
    private boolean active = false;
    private long expireAt = -1;

    public PowerUp(String type) {
        super("powerup", 0, 0);
        this.animKey = "powerup" + type;
        this.anim = AssetManager.getInstance().getAnimation(animKey);
        if (anim == null) {
            System.err.println("[ERROR] ANIMATION IS NULL");
        }
        System.out.println("Type: " + type);
        this.type = type;
    }

    public PowerUp(String type, String animKey) {
        super("powerup", 0, 0);
        this.animKey = animKey;
        this.anim = AssetManager.getInstance().getAnimation(animKey);
        if (anim == null) {
            System.err.println("[ERROR] ANIMATION IS NULL");
        }

        System.out.println("Type: " + type);
        this.type = type;
    }

    public void dropFrom(Brick brick) {
        setPosition(brick.getX(), brick.getY());
        visible = true;
    }

    public void fall(double deltaTime) {
        if (!visible) return;
        y += fallSpeed * deltaTime;
        setPosition(x, y);
        if (y > GlobalVar.HEIGHT) visible = false;
    }

    public void activate(long durationMillis) {
        active = true;
        expireAt = System.currentTimeMillis() + durationMillis;
    }

    public void activateWithRemainingDuration(long remainingDuration) {
        if (remainingDuration > 0) {
            active = true;
            expireAt = System.currentTimeMillis() + remainingDuration;
        }
    }

    public long getRemainingDuration() {
        if (!isActive()) {
            return 0;
        }
        // Calculate remaining time, ensuring it's not negative
        long remaining = expireAt - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public boolean hasExpired() {
        return active && System.currentTimeMillis() > expireAt;
    }

    public boolean isActive() {
        return active && System.currentTimeMillis() < expireAt;
    }

    public void deactivate() { active = false; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public String getType() { return type; }
}
