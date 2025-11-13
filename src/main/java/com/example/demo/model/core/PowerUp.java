package com.example.demo.model.core;

import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.core.gameobjects.AnimatedObject;
import com.example.demo.model.state.PowerUpData;
import com.example.demo.utils.var.GlobalVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerUp extends AnimatedObject<PowerUpData> {
    private static final Logger log = LoggerFactory.getLogger(PowerUp.class);
    private String type;
    private final double fallSpeed = 150.0; // TODO: be a constant
    private boolean active = false;
    private long expireAt = -1;

    public PowerUp(String type) {
        super("powerup_" + type.toLowerCase() , 0, 0);
        PowerUp.log.info("Type: {}", type);
        this.type = type;
    }

    public void dropFrom(Brick brick) {
        setPosition(brick.getX() + brick.getWidth() / 2, brick.getY() + brick.getHeight() / 2);
        visible = true;
    }

    public void fall(double deltaTime) {
        if (!visible) return;
        setPosition(getX(), getY() + fallSpeed * deltaTime);
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
    public String getType() { return type; }
    public void reset(String type) {
        this.type = type;
        setPosition(0,0);
        setVisible(false);
        active = false;
        expireAt = 1000;
    }

    @Override
    public void applyState(PowerUpData AnimationObjectData) {}
}
