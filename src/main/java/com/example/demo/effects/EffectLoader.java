package com.example.demo.effects;

import com.example.demo.animation.Animation;
import com.example.demo.animation.AnimationFactory;

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

        switch(animationKey) {
            case "explosion":
                effect = new ExplosionEffect(x, y, durationSeconds, cloneAnimation);
                break;
            // Add more effects here as needed

            default:
                throw new IllegalArgumentException("Effect key not found: " + animationKey);
        }

        // Set size if specified
        if (width > 0 && height > 0) {
            effect.setSize(x, y);
        }

        return effect;
    }
}
