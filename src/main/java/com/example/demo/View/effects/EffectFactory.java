package com.example.demo.View.effects;

import java.util.HashMap;
import java.util.Map;

public class EffectFactory {
    private static final EffectFactory      instance    = new EffectFactory();
    private final Map<String, EffectLoader> effects     = new HashMap<>();

    // Constructor
    EffectFactory() {
        loadAllEffects();
    }

    // Get the singleton instance
    public static EffectFactory getInstance() {
        return instance;
    }

    // Get effect by key
    public Effect getEffect(String key, double x, double y, double durationSeconds) {
        if (!effects.containsKey(key)) {
            throw new IllegalArgumentException("Effect key not found: " + key);
        }

        return effects.get(key).load(x, y, durationSeconds);
    }

    // Load all effects
    private void loadAllEffects() {
        try {
            effects.put(
                "explosion",
                new EffectLoader("explosion").setSize(64, 64)
            );

            // Add more effects here as needed

        } catch (Exception e) {
            System.err.println("Error loading effects: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
