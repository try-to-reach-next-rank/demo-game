package com.example.demo.View.effects;

import com.example.demo.Controller.core.Vector2D;
import com.example.demo.Controller.utils.Timer;

public abstract class VisualEffect implements Effect {
    protected Vector2D      position;
    protected double        width  = -1;
    protected double        height = -1;
    protected boolean       active;
    protected final Timer   timer;

    // Constructor
    public VisualEffect(double x, double y, double durationSeconds) {
        position = new Vector2D(x, y);
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
    public Vector2D getPosition() {
        return this.position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    // Size setters
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    // Reset effect to use again
    public void reset(double x, double y, double durationSeconds) {
        setPosition(new Vector2D(x, y));
        this.timer.start(durationSeconds);
        activate();
    }

    // Abstract methods to be implemented by subclasses
    @Override
    public abstract void draw(javafx.scene.canvas.GraphicsContext gc);
}
