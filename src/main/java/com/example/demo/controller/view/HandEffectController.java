package com.example.demo.controller.view;

import com.example.demo.model.core.Ball;
import com.example.demo.model.core.effects.HandGrabEffect;
import com.example.demo.utils.ObjectPool;

public class HandEffectController {
    private static final HandEffectController instance = new HandEffectController();

    private final ObjectPool<HandGrabEffect> handPool;
    private HandGrabEffect activeHandEffect = null;

    private HandEffectController() {
        this.handPool = new ObjectPool<>(HandGrabEffect::new, 2);
    }

    public static HandEffectController getInstance() {
        return instance;
    }

    /**
     * Trigger hand grab effect on the ball
     * @param ball The ball to grab and throw
     */
    public void triggerHandGrab(Ball ball) {
        if (ball == null) return;

        if (activeHandEffect != null && activeHandEffect.isActive()) {
            return;
        }
        activeHandEffect = handPool.acquire();
        activeHandEffect.activateOnBall(ball);
        registerWithRenderer(activeHandEffect);
    }

    /**
     * Update active hand effects
     */
    public void update(double deltaTime) {
        if (activeHandEffect != null) {
            activeHandEffect.update(deltaTime);

            // Return to pool when done
            if (!activeHandEffect.isActive()) {
                handPool.release(activeHandEffect);
                activeHandEffect = null;
            }
        }
    }

    /**
     * Check if hand effect is currently active
     */
    public boolean isActive() {
        return activeHandEffect != null && activeHandEffect.isActive();
    }

    /**
     * Get current active effect (for rendering)
     */
    public HandGrabEffect getActiveEffect() {
        return activeHandEffect;
    }

    /**
     * Register effect with EffectRenderer
     * This is a helper method - you may need to modify EffectRenderer
     */
    private void registerWithRenderer(HandGrabEffect effect) {
        // For now, we'll handle rendering separately
        // You can integrate this with EffectRenderer.getInstance()
    }

    public void reset() {
        if (activeHandEffect != null) {
            activeHandEffect.deactivate();
            handPool.release(activeHandEffect);
            activeHandEffect = null;
        }
    }
}
