package com.example.demo.controller.view;

import com.example.demo.model.core.effects.CloudEffect;
import com.example.demo.utils.ObjectPool;

public class CloudEffectController {
    private static final CloudEffectController instance = new CloudEffectController();

    private final ObjectPool<CloudEffect> cloudPool;
    private CloudEffect activeCloudEffect = null;

    private CloudEffectController() {
        this.cloudPool = new ObjectPool<>(CloudEffect::new, 2);
    }

    public static CloudEffectController getInstance() {
        return instance;
    }

    /**
     * Trigger cloud effect manually (e.g., by key press or SwitchBrick)
     */
    public void triggerCloudEffect(double screenWidth, double screenHeight) {
        if (activeCloudEffect != null && activeCloudEffect.isActive()) {
            return; // already running
        }

        activeCloudEffect = cloudPool.acquire();
        activeCloudEffect.activate(screenWidth, screenHeight, 0);
        registerWithRenderer(activeCloudEffect);
    }

    /**
     * Update cloud effect state
     */
    public void update(double deltaTime) {
        if (activeCloudEffect != null) {
            activeCloudEffect.update(deltaTime);

            if (!activeCloudEffect.isActive()) {
                cloudPool.release(activeCloudEffect);
                activeCloudEffect = null;
            }
        }
    }

    /**
     * Check if active
     */
    public boolean isActive() {
        return activeCloudEffect != null && activeCloudEffect.isActive();
    }

    /**
     * Get current active effect (for rendering)
     */
    public CloudEffect getActiveEffect() {
        return activeCloudEffect;
    }

    private void registerWithRenderer(CloudEffect effect) {
        // optional: hook into EffectRenderer if you want it automatically rendered there
    }

    public void reset() {
        if (activeCloudEffect != null) {
            activeCloudEffect.deactivate();
            cloudPool.release(activeCloudEffect);
            activeCloudEffect = null;
        }
    }
}
