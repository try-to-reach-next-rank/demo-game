package com.example.demo.model.core;

import com.example.demo.controller.AssetManager;
import com.example.demo.engine.Effect;
import com.example.demo.model.utils.Animation;
import com.example.demo.model.utils.Timer;

import javafx.scene.canvas.GraphicsContext;

public abstract class VisualEffect implements Effect {
    protected double x, y;
    protected boolean active;
    protected final Timer timer;
    protected double durationSeconds;

    public VisualEffect() {
        this.active = false;
        this.timer = new Timer();
        this.durationSeconds = 100000.0;
    }

    @Override
    public void update(double deltaTime) {
        if (!active) return;
        timer.update(deltaTime);
        if (timer.isFinished()) deactivate();
    }

    @Override
    public abstract void render(GraphicsContext gc);

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void activate(double x, double y, double durationSeconds) {
        timer.reset();
        this.x = x;
        this.y = y;
        this.durationSeconds = durationSeconds;
        this.timer.start(durationSeconds);
        this.active = true;
        onActivate();
    }

    protected void onActivate() {
        // Default do nothing
    }

    @Override
    public void deactivate() {
        if (!active) return;
        active = false;
        onDeactivate();
    }

    protected void onDeactivate() {
        // Default do nothing
    }

    public abstract VisualEffect clone();
}
