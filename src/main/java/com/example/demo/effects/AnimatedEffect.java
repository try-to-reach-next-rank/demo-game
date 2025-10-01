package com.example.demo.effects;

import javafx.scene.canvas.GraphicsContext;

import com.example.demo.utils.Animation;
import com.example.demo.core.VARIABLES;

public abstract class AnimatedEffect extends VisualEffect {
    protected final Animation animation;

    // Constructor
    public AnimatedEffect(double x, double y, double durationSeconds, Animation animation) {
        super(x, y, durationSeconds);
        this.animation = animation;
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
        if (!isActive() || animation == null) return;

        gc.drawImage(animation.getCurrentFrame(), this.x, this.y);
    }
    
}
