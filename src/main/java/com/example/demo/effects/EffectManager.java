package com.example.demo.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class EffectManager {
    // Singleton instance
    private static final EffectManager instance = new EffectManager();

    // List of active effects
    private final List<Effect> allEffects = new ArrayList<>();
    
    // Private constructor to prevent instantiation
    private EffectManager() {}

    // Get the singleton instance
    public static EffectManager getInstance() {
        return instance;
    }

    // Add a new effect
    public void addEffect(Effect effect) {
        allEffects.add(effect);
    }

    // Remove an effect
    public void removeEffect(Effect effect) {
        if (allEffects.contains(effect)) {
            allEffects.remove(effect);
        } else {
            // throw new Illegal
        }
    }

    // Update all effects
    public void updateEffects() {
        Iterator<Effect> iterator = allEffects.iterator();

        while (iterator.hasNext()) {
            Effect effect = iterator.next();

            // If effect is active, update it
            // if (effect.isActive()) {
            //     effect.update();
            // }
        }
    }

    // Draw all effects
    public void drawEffects(GraphicsContext gc) {
        for (Effect effect : allEffects) {
            if (effect.isActive()) {
                effect.draw(gc);
            }
        }
    }

    // Clear all effects
    public void clearEffects() {
        allEffects.clear();
    }
}
