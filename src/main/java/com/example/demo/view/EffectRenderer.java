package com.example.demo.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.demo.view.effects.EffectFactory;
import com.example.demo.view.effects.VisualEffect;

import javafx.scene.canvas.GraphicsContext;

public class EffectRenderer {
    /*
    TODO: split this class into
    - effect state manager: tracks active effects in the game, duration, applies game logic
    - renderer:             reads state manager to know which visual effect to draw and how
     */
    // Singleton instance
    private static final EffectRenderer instance = new EffectRenderer();

    // List of active effects
    private final List<VisualEffect> activeEffects = new ArrayList<>();
    
    // Private constructor to prevent instantiation
    private EffectRenderer() {}

    // Get the singleton instance
    public static EffectRenderer getInstance() {
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
