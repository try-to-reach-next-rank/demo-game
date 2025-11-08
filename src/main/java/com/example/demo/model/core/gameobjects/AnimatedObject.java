package com.example.demo.model.core.gameobjects;

import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.state.gameobjectdata.AnimatedObjectData;
import com.example.demo.utils.Animation;

public abstract class AnimatedObject<T extends AnimatedObjectData> extends GameObject<T> {
    protected String animKey;
    protected Animation animation;

    public AnimatedObject(String animKey, double startX, double startY) {
        super(startX, startY);
        setAnimationKey(animKey);
    }

    public void setAnimationKey(String animKey) {
        Animation anim = AssetManager.getInstance().getAnimation(animKey);
        if (anim == null) {
            System.err.println("[ERROR] Animation not found for key: " + animKey);
            return;
        }

        this.animKey = animKey;
        this.animation = anim;
        this.width = animation.getRenderWidth();
        this.height = animation.getRenderHeight();
        this.baseWidth = width;
        this.baseHeight = height;
        applyScale();
    }

    public void updateAnimation(double deltaTime) {
        if (animation == null) {
            System.err.println("[WARN] Animation is null, cannot update: " + animKey);
            return;
        }

        animation.update(deltaTime);
    }

    public String getAnimationKey() { return this.animKey; }
    public Animation getAnimation() { return this.animation; }

    @Override
    public abstract void applyState(T AnimationObjectData);
}
