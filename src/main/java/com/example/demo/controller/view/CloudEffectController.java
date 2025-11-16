package com.example.demo.controller.view;

import com.example.demo.model.core.effects.CloudEffect;
import com.example.demo.utils.ObjectPool;
import com.example.demo.utils.Timer;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import javafx.scene.input.KeyCode;

public class CloudEffectController {
    private static final CloudEffectController instance = new CloudEffectController();

    private final ObjectPool<CloudEffect> cloudPool;
    private CloudEffect activeCloudEffect = null;

    private CloudEffectController() {
        this.cloudPool = new ObjectPool<>(CloudEffect::new, 2);
        // TODO: trigger cloud with C no more timer
    }

    public static CloudEffectController getInstance() {
        return instance;
    }

    /**
     * Update cloud effect state
     */
    public void update(double deltaTime) {
        // Update active cloud
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

    public void triggerCloud() {
        activeCloudEffect = cloudPool.acquire();
        activeCloudEffect.activate(GlobalVar.WIDTH, GlobalVar.HEIGHT, 0);
    }

    public void reset() {
        if (activeCloudEffect != null) {
            activeCloudEffect.deactivate();
            cloudPool.release(activeCloudEffect);
            activeCloudEffect = null;
        }
    }
}
