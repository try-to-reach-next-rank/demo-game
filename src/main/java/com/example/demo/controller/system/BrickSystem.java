package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.graphics.BrickTextureProvider;
import com.example.demo.view.EffectRenderer;

import java.util.List;
import java.util.function.Consumer;

/**
 * Handles brick damage, explosion propagation, and power-up spawning.
 * TODO: Add handle reveal bricks in here
 */
public class BrickSystem implements Updatable {
    private final List<Brick> bricks;
    private Consumer<Brick> onBrickDestroyed;
    private final PowerUpSystem powerUpSystem;

    public BrickSystem(List<Brick> bricks, SystemManager systemManager) {
        this.bricks = bricks;
        this.powerUpSystem = systemManager.get(PowerUpSystem.class);
    }

    @Override
    public void update(double deltaTime) {
        bricks.removeIf(Brick::isDestroyed);
    }

    @Override
    public void clear() {
        // TODO: CLEAR
    }

    public void handleCollision(Brick brick, GameObject obj) {
        if (obj instanceof Ball ball) {
            handleBallCollision(brick, ball);
        }
    }

    /**
     * Handles a collision between a ball and a brick.
     */
    private void handleBallCollision(Brick brick, Ball ball) {
        if (brick.isDestroyed()) return;
        if (ball == null) return;
        applyDamage(brick, ball);

        // Check destruction and trigger effects
        if (brick.isDestroyed()) {
            spawnDestructionEffect(brick);
            powerUpSystem.maybeSpawnPowerUp(brick);
        }
    }

    /**
     * Applies damage to a brick and updates its texture.
     */
    private void applyDamage(Brick brick, Ball ball) {
        if (brick.getHealth() == Integer.MAX_VALUE) return;
        int damage = (ball.isStronger()) ? GameVar.MAXPOWER : GameVar.MINPOWER;
        boolean destroyed = brick.takeDamage(damage);

        // Not destroy, play sound
        if (!destroyed) {
            Sound.getInstance().playSound("brick_hit");
            return;
        }

        // Destroy, spawn effect + maybe powerup
        spawnDestructionEffect(brick);
        powerUpSystem.maybeSpawnPowerUp(brick);
        spawnScorePopupEffect(brick);

        if (onBrickDestroyed != null) {
            onBrickDestroyed.accept(brick); // notify external systems
        }
    }

    /**
     * Creates a visual explosion effect when a brick is destroyed.
     */
    private void spawnDestructionEffect(Brick brick) {
        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight();
        EffectRenderer.getInstance().spawn(GameVar.EXPLOSION1_EFFECT_KEY, centerX, centerY, GameVar.EFFECT_DURATION);
    }

    public int getRemainingBricksCount() {
        if (bricks == null || bricks.size() == 0) {
            return 0;
        }

        int count = 0;
        for (Brick brick : bricks) {
            // Only count bricks that are:
            // 1. Not destroyed
            // 2. Not indestructible (health != Integer.MAX_VALUE)
            if (!brick.isDestroyed() && brick.getHealth() != Integer.MAX_VALUE) {
                count++;
            }
        }
        return count;
    }

    public boolean isLevelComplete() {
        return getRemainingBricksCount() == 0;
    }
    /**
     * Creates a score popup effect at the brick's location.
     */
    private void spawnScorePopupEffect(Brick brick) {
        String scoreText = "+" + brick.getScoreValue();
        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight() / 2;
        // Gọi spawn với tham số text
        EffectRenderer.getInstance().spawn("scorePopup", centerX, centerY, 1.0);
    }

    public void setOnBrickDestroyed(Consumer<Brick> onBrickDestroyed) {
        this.onBrickDestroyed = onBrickDestroyed;
    }
}
