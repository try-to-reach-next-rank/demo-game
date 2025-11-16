package com.example.demo.controller.view;

import com.example.demo.model.core.effects.CloudEffect;
import com.example.demo.utils.ObjectPool;
import com.example.demo.utils.Timer;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

public class CloudEffectController {
    private static final CloudEffectController instance = new CloudEffectController();

    private final ObjectPool<CloudEffect> cloudPool;
    private CloudEffect activeCloudEffect = null;
    private final Timer spawnTimer;

    private CloudEffectController() {
        this.cloudPool = new ObjectPool<>(CloudEffect::new, 2);
        // TODO: trigger cloud with C no more timer
        this.spawnTimer = new Timer();
        this.spawnTimer.start(20.0); // first cloud after 20 seconds
    }

    public static CloudEffectController getInstance() {
        return instance;
    }

    /**
     * Update cloud effect state
     */
    public void update(double deltaTime) {
        spawnTimer.update(deltaTime);

        // Spawn a new cloud automatically if timer finished and no cloud active
        if (spawnTimer.isFinished() && (activeCloudEffect == null || !activeCloudEffect.isActive())) {
            activeCloudEffect = cloudPool.acquire();
            activeCloudEffect.activate(GlobalVar.WIDTH, GlobalVar.HEIGHT, 0);

            spawnTimer.start(50.0); // reset timer for next spawn
        }

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

    public void reset() {
        if (activeCloudEffect != null) {
            activeCloudEffect.deactivate();
            cloudPool.release(activeCloudEffect);
            activeCloudEffect = null;
        }
    }
}
