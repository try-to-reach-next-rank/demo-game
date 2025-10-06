package com.example.demo.view.effects;

import com.example.demo.view.animation.Animation;

public class ExplosionEffect extends AnimatedEffect {
    // Constructor
    public ExplosionEffect(double x, double y, double durationSeconds, Animation animation) {
        super(x, y, durationSeconds, animation);
    }

    // Override activate and deactivate methods for update sounds
    @Override
    public void activate() {
        super.activate();
        // Play explosion sound
    }

    // (Optional) If you want explosion to play a sound when reset:
    @Override
    public void reset(double x, double y, double durationSeconds) {
        super.reset(x, y, durationSeconds);

        // Play explosion sound effect (optional)
        // SoundManager.getInstance().playSound("explosion_hit");
    }

    // (Optional) reset with a new animation
    // public void reset(double x, double y, double durationSeconds, Animation animation) {
    //     super.reset(x, y, durationSeconds, animation);

    //     // Play sound again if needed
    //     // SoundManager.getInstance().playSound("explosion_hit");
    // }
}
