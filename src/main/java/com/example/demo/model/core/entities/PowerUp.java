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
        super("powerup_" + type.toLowerCase() , 0, 0);
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
        setAnimationKey("powerup_" + type.toLowerCase());

        // Reset visibility and state
        setVisible(false);     // not yet dropped
        active = false;        // not active until picked up or triggered

        // Reset position
        setPosition(0, 0);     // or some default spawn point if needed

        // Reset timers and duration tracking
        applyDuration = 0;
        expireAt = -1;
    }

    public double getApplyDurationLeft() { return this.applyDuration; }
    public void  setApplyDuration(double applyDuration) { this.applyDuration = applyDuration; }

    @Override
    public void applyState(PowerUpData data) {
        super.applyState(data);
    }
}
