package com.example.demo.model.core;

import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.utils.GlobalVar;

public class PowerUp extends GameObject {
    private final String type;
    private boolean visible;
    private final double fallSpeed = 150.0;
    private boolean active = false;
    private long expireAt = -1;

    public PowerUp(String type) {
        super("/images/fastup.png", 0, 0);
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
