package com.example.demo.effects;

import javafx.scene.canvas.GraphicsContext;

import com.example.demo.utils.Animation;
import com.example.demo.core.VARIABLES;

public abstract class AnimatedEffect extends VisualEffect {
    protected Animation animation;

    // Constructor
    public AnimatedEffect(double x, double y, double durationSeconds, Animation animation) {
        super(x, y, durationSeconds);
        this.animation = animation;
    }

    // Override activate, deactivate to start/stop animation
    @Override
    public void activate() {
        super.activate();
        if (animation != null) {
            this.animation.reset();
            this.animation.start();
        }
    }

    // Update the animation
    @Override
    public void update() {
        if (!isActive() || animation == null) return;

        double fps = VARIABLES.FPS;
        double deltaTime = 1.0 / fps;
        animation.update(deltaTime);

        // Deactivate the effect if the timer has finished
        if (timer.isFinished()) {
            deactivate();
        }
    }

    // Draw the current frame of the animation
    @Override
    public void draw(GraphicsContext gc) {
        // If not active or no animation, skip drawing
        if (!isActive() || animation == null) return;

        // If current frame is null, skip drawing
        if (animation.getCurrentFrame() == null) return;

        gc.drawImage(animation.getCurrentFrame(), this.position.x, this.position.y);
    }
    
    // Reset to reuse
    @Override
    public void reset(double x, double y, double durationSeconds) {
        super.reset(x, y, durationSeconds);
        if (animation != null) {
            this.animation.reset();
            this.animation.start();
        }
    }

    // Reset to reuse with new animation
    public void reset(double x, double y, double durationSeconds, Animation animation) {
        reset(x, y, durationSeconds);
        this.animation = animation;

        if (this.animation != null) {
            this.animation.reset();
            this.animation.start();
        }
    }
}
