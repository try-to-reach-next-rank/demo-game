package com.example.demo.effects;

import com.example.demo.utils.Timer;

public abstract class VisualEffect implements Effect {
    protected double x, y;
    protected boolean active;
    protected final Timer timer;

    // Constructor
    public VisualEffect(double x, double y, double durationSeconds) {
        this.x = x;
        this.y = y;
        this.timer = new Timer();
        this.timer.start(durationSeconds);
        this.active = true;
    }

    // Duration override
    @Override
    public double duration() {
        return timer.getDuration();
    }

    // Override isActive to check timer
    @Override
    public boolean isActive() {
        return active && timer.isRunning();
    }

    // Activate the effect
    @Override
    public void activate() {
        if (!active) {
            this.active = true;
            this.timer.start(duration());
        }
    }

    // Deactivate the effect
    @Override
    public void deactivate() {
        this.active = false;
        this.timer.stop();
    }

    // Position getters and setters
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Abstract methods to be implemented by subclasses
    @Override
    public abstract void draw(javafx.scene.canvas.GraphicsContext gc);
}
