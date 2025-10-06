package com.example.demo.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.demo.view.effects.EffectFactory;
import com.example.demo.view.effects.VisualEffect;

import javafx.scene.canvas.GraphicsContext;

public class EffectManager {
    // Singleton instance
    private static final EffectManager instance = new EffectManager();

    // List of active effects
    private final List<VisualEffect> activeEffects = new ArrayList<>();
    
    // Private constructor to prevent instantiation
    private EffectManager() {}

    // Get the singleton instance
    public static EffectManager getInstance() {
        return instance;
    }

    // Spawn effect by key
    public void spawnEffect(String name, double x, double y, double durationSeconds) {
        // If effect has already existed and being inactive
        // Add key to each effect
        
        // Add new effect
        VisualEffect newEffect = (VisualEffect) EffectFactory.getInstance().getEffect(name, x, y, durationSeconds);
        activeEffects.add(newEffect);
    }

    // Update all effects
    public void update(double deltaTime) {
        Iterator<VisualEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            VisualEffect effect = iterator.next();
            if (effect.isActive()) {
                effect.update(deltaTime);
            } else {
                iterator.remove(); // Remove inactive effects
            }
        }
    }

    // Draw all effects
    public void draw(GraphicsContext gc) {
        for (VisualEffect effect : activeEffects) {
            if (effect.isActive()) {
                effect.draw(gc);
            }
        }
    }

    // Clear all effects
    public void clear() {
        activeEffects.clear();
    }
}
