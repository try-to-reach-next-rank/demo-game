package com.example.demo.model.state.gameobjectdata;

import com.example.demo.model.core.gameobjects.AnimatedObject;

public class AnimatedObjectData extends GameObjectData {
    private final String animKey;
    private final int currentFrame;

    public AnimatedObjectData(AnimatedObject<?> object) {
        super(object);
        this.animKey = object.getAnimationKey();
        this.currentFrame = object.getAnimation() != null ? object.getAnimation().getCurrentFrame() : -1;
    }

    public String getAnimKey() { return animKey; }
    public int getCurrentFrame() { return currentFrame; }
}
