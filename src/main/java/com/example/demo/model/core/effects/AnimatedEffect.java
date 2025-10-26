package com.example.demo.model.core.effects;

import com.example.demo.controller.view.AssetManager;
import com.example.demo.model.core.VisualEffect;
import com.example.demo.model.utils.Animation;
import javafx.scene.canvas.GraphicsContext;

public class AnimatedEffect extends VisualEffect {
    protected String animKey;
    private Animation animation;

    public AnimatedEffect(String animKey) {
        super();
        this.animKey = animKey;
        this.animation = AssetManager.getInstance().getAnimation(animKey);
    }

    @Override
    public void onActivate() {
        if (animation.isLoop()) return;
        animation.setDuration(durationSeconds);
        animation.restart();
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive()) return;
        animation.update(deltaTime);
        super.update(deltaTime);
        
        if (animation.isFinished()) {
            deactivate();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive()) return;

        animation.render(gc, x, y);
    }

    @Override
    public void onDeactivate() {
        animation.stop();
    }

    @Override
    public AnimatedEffect clone() {
        return new AnimatedEffect(animKey);
    }
}

