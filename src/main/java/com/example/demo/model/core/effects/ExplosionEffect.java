package com.example.demo.model.core.effects;

public class ExplosionEffect extends AnimatedEffect {

    public ExplosionEffect(String animKey) {
        super(animKey);
    }

    @Override
    public void activate(double x, double y, double durationSeconds) {
        super.activate(x, y, durationSeconds);
        // TODO: play sound here
    }
}