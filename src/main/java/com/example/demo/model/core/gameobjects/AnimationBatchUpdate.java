package com.example.demo.model.core.gameobjects;

import com.example.demo.utils.Animation;

import java.util.HashSet;
import java.util.Set;

public class AnimationBatchUpdate {
    private static final AnimationBatchUpdate instance = new AnimationBatchUpdate();
    private final Set<Animation> activeAnimations = new HashSet<>();

    public static AnimationBatchUpdate getInstance() {
        return instance;
    }

    public void register(Animation animation) {
        if (animation != null) activeAnimations.add(animation);
    }

    public void updateAll(double deltaTime) {
        for (Animation anim : activeAnimations) {
            anim.update(deltaTime);
        }
    }

    public void clear() {
        activeAnimations.clear();
    }
}
