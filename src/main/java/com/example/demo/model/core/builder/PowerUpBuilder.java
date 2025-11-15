package com.example.demo.model.core.builder;

import com.example.demo.model.core.entities.PowerUp;

public class PowerUpBuilder {
    private String type;

    private double x = 0;
    private double y = 0;
    private boolean visible = false;
    private boolean active = false;
    private long remainingDuration = 0;

    public PowerUpBuilder() {}

    public PowerUpBuilder type(String type) {
        this.type = type;
        return this;
    }

    /** Set the position of the PowerUp */
    public PowerUpBuilder position(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /** Set whether the PowerUp is initially visible */
    public PowerUpBuilder visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /** Set whether the PowerUp is active initially with a duration */
    public PowerUpBuilder active(boolean active, long durationMillis) {
        this.active = active;
        this.remainingDuration = durationMillis;
        return this;
    }

    /**
     * Build the PowerUp instance.
     * @return configured PowerUp
     */
    public PowerUp build() {
        if (type == null || type.isEmpty()) {
            throw new IllegalStateException("PowerUp type must be set");
        }

        // Create the object
        PowerUp powerUp = new PowerUp(type);

        // Apply optional settings
        powerUp.setPosition(x, y);
        powerUp.setVisible(visible);

        if (active) {
            powerUp.activateWithRemainingDuration(remainingDuration);
        }

        return powerUp;
    }
}
