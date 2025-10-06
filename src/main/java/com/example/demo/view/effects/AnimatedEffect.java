package com.example.demo.view.effects;

import javafx.scene.canvas.GraphicsContext;

import com.example.demo.view.animation.Animation;

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
    public void update(double deltaTime) {
        if (!isActive() || animation == null) return;

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
        var frame = animation.getCurrentFrame();

        if (frame == null) return;

        double drawWidth  = (width  > 0) ? width  : frame.getWidth();
        double drawHeight = (height > 0) ? height : frame.getHeight();


        gc.drawImage(frame, this.position.x, this.position.y, drawWidth, drawHeight);
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
    // public void reset(double x, double y, double durationSeconds, Animation animation) {
    //     reset(x, y, durationSeconds);
    //     this.animation = animation;

    //     if (this.animation != null) {
    //         this.animation.reset();
    //         this.animation.start();
    //     }
    // }
}
