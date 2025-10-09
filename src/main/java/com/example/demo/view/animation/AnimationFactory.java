package com.example.demo.view.animation;

import java.util.HashMap;
import java.util.Map;

public class AnimationFactory {
    private static final AnimationFactory       instance    = new AnimationFactory();
    private final Map<String, Animation>  animations  = new HashMap<>();

    // Constructor
    AnimationFactory() {
        loadAllAnimations();
    }

    // Get the singleton instance
    public static AnimationFactory getInstance() {
        return instance;
    }

    // Get animation by key
    public Animation getAnimation(String key) {
        if (!animations.containsKey(key)) {
            throw new IllegalArgumentException("Animation key not found: " + key);
        }

        return animations.get(key);
    }
    
    // Load all animations
    private void loadAllAnimations() {
        try {
            animations.put(
                "explosion", 
                new AnimationLoader("/images/effects/explosion", 24).load()
            );

            animations.put(
                "explosion2", 
                new AnimationLoader("/images/effects/explosion2", 32).load()
            );

            // Add more animations here as needed

        } catch (Exception e) {
            System.err.println("Error loading animations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
