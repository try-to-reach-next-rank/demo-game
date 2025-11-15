package com.example.demo.model.core.entities;

import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.gameobjects.AnimatedObject;
import com.example.demo.model.state.PowerUpData;
import com.example.demo.utils.var.GameVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerUp extends AnimatedObject<PowerUpData> {
    private static final Logger log = LoggerFactory.getLogger(PowerUp.class);
    private String type;
    private final double fallSpeed = GameVar.BASE_SPEED_POWERUP;
    private boolean active = false;
    
    // TODO: MAKE A TIMER FOR PU
    private double applyDuration;
    private long expireAt = -1;

    public PowerUp(String type) {
        super(0, 0);
        setAnimationKey("powerup_" + type.toLowerCase());
        log.info("Type: {}", type);
        this.type = type;
    }

    public void dropFrom(Brick brick) {
        setPosition(brick.getX() + brick.getWidth() / 2, brick.getY() + brick.getHeight() / 2);
        setVisible(true);
    }

    public void fall(double deltaTime) {
        if (!isVisible()) return;
        y += fallSpeed * deltaTime;
        if (y > GameVar.MAP_MAX_Y) setVisible(false);
    }

    public void activate(long durationMillis) {
        active = true;
        expireAt = System.currentTimeMillis() + durationMillis; // ms
    }

    public void activateWithRemainingDuration(long remainingDuration) {
        if (remainingDuration > 0) {
            active = true;
            expireAt = System.currentTimeMillis() + remainingDuration; //ms
        }
    }

    public long getRemainingDuration() {
        if (!isActive()) {
            return 0;
        }
        long remaining = expireAt - System.currentTimeMillis();
        return Math.max(0, remaining); //ms
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
        setAnimationKey("powerup_" + type.toLowerCase());
        setPosition(0,0);
        setVisible(false);
        active = false;
        expireAt = -1;
    }

    public double getApplyDurationLeft() { return this.applyDuration; }
    public void  setApplyDuration(double applyDuration) { this.applyDuration = applyDuration; }

    @Override
    public void applyState(PowerUpData data) {
        super.applyState(data);
    }
}
