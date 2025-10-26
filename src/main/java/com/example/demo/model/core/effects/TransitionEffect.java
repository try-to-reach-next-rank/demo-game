package com.example.demo.model.core.effects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TransitionEffect {

    private double duration;
    private double time;
    private boolean active;
    private boolean midpointReached;
    private double alpha;

    private Runnable onMidpoint;
    private Runnable onEnd;
    private Color color = Color.BLACK;

    public TransitionEffect(double durationSeconds) {
        this.duration = Math.max(0.01, durationSeconds);
    }

    public void start(Runnable onMidpoint, Runnable onEnd) {
        this.time = 0;
        this.active = true;
        this.midpointReached = false;
        this.onMidpoint = onMidpoint;
        this.onEnd = onEnd;
    }

    public void update(double deltaTime) {
        if (!active) return;

        time += deltaTime;
        double half = duration / 2.0;

        if (!midpointReached && time >= half) {
            midpointReached = true;
            if (onMidpoint != null) onMidpoint.run();
        }

        if (time >= duration) {
            active = false;
            if (onEnd != null) onEnd.run();
        }
    }

    public void render(GraphicsContext gc, double width, double height) {
        if (!active) return;

        double half = duration / 2.0;
        alpha = (time < half)
                ? (time / half)
                : (1 - (time - half) / half);

        alpha = Math.max(0, Math.min(1, alpha));

        gc.setGlobalAlpha(alpha);
        gc.setFill(color);
        gc.fillRect(0, 0, width, height);
        gc.setGlobalAlpha(1.0);
    }

    public boolean isActive() { return active; }

    public double getCurrentAlpha() {
        return alpha;
    }
}