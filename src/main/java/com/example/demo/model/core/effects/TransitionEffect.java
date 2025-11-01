package com.example.demo.model.core.effects;

import com.example.demo.utils.var.GameVar;

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
    private Color color = GameVar.TRANSITION_DEFAULT_COLOR;

    public TransitionEffect(double durationSeconds) {
        this.duration = Math.max(GameVar.TRANSITION_MIN_DURATION, durationSeconds);
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
        double half = duration / GameVar.TRANSITION_HALF_FACTOR;

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

        double half = duration / GameVar.TRANSITION_HALF_FACTOR;
        alpha = (time < half)
                ? (time / half)
                : (1 - (time - half) / half);

        alpha = Math.max(0, Math.min(GameVar.TRANSITION_OPACITY_FULL, alpha));

        gc.setGlobalAlpha(alpha);
        gc.setFill(color);
        gc.fillRect(0, 0, width, height);
        gc.setGlobalAlpha(GameVar.TRANSITION_OPACITY_FULL);
    }

    public boolean isActive() { return active; }

    public double getCurrentAlpha() {
        return alpha;
    }
}