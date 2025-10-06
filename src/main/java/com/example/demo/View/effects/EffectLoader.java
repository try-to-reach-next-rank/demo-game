package com.example.demo.View.effects;

import com.example.demo.View.animation.Animation;
import com.example.demo.View.animation.AnimationFactory;

public class EffectLoader {
    private String animationKey;
    private boolean loop = false;
    private double  width        = -1;
    private double  height       = -1;

    // Constructor
    public EffectLoader(String animationKey) {
        this.animationKey = animationKey;
    }

    // Set size
    public EffectLoader setSize(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    // Set loop
    public EffectLoader setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    // Load effect for animated effects from animation factory
    public VisualEffect load(double x, double y, double durationSeconds) {
        Animation animation = AnimationFactory.getInstance().getAnimation(animationKey);

        Animation cloneAnimation = new Animation(
            animation.getFrames(), animation.getFrameDuration(), loop);

        VisualEffect effect;

        // Calculate center position
        double centerX = x - this.width / 2.0;
        double centerY = y - this.height / 2.0;

        switch(animationKey) {
            case "explosion":
                effect = new ExplosionEffect(centerX, centerY, durationSeconds, cloneAnimation);
                break;
            // Add more effects here as needed

            default:
                throw new IllegalArgumentException("Effect key not found: " + animationKey);
        }

        // Set size if specified
        if (width > 0 && height > 0) {
            effect.setSize(this.width, this.height);
        }

        return effect;
    }
}
