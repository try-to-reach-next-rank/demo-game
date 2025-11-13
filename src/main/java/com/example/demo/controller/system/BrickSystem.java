package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.factory.PowerUpFactory;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.EffectRenderer;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Handles brick damage, explosion propagation, and power-up spawning.
 * TODO: Add handle reveal bricks in here
 */
public class BrickSystem implements Updatable {
    private final Brick[] bricks;
    private final List<PowerUp> powerUps;
    private final Random random = new Random();
    private Consumer<Brick> onBrickDestroyed;

    public BrickSystem(Brick[] bricks, List<PowerUp> powerUps) {
        this.bricks = bricks;
        this.powerUps = powerUps;
    }

    @Override
    public void update(double deltaTime) {

    }

    public void onBallHitBrick(Brick brick, Ball ball) {
        if (brick.isDestroyed()) return;
        if (ball == null) return;

        int damage = ball.isStronger() ? GameVar.MAXPOWER : GameVar.MINPOWER;
        boolean destroyed = brick.takeDamage(damage);

        if (destroyed) {
            spawnDestructionEffect(brick);
            maybeSpawnPowerUp(brick);
            if (onBrickDestroyed != null){
                onBrickDestroyed.accept(brick);

            }
        }
    }

    /**
     * Spawns a power-up at the destroyed brick's location.
     */
    private void maybeSpawnPowerUp(Brick brick) {
        PowerUp powerUp = PowerUpFactory.createRandomPowerUp(
                brick.getX() + brick.getWidth() / 2,
                brick.getY() + brick.getHeight() / 2
        );

        powerUps.add(powerUp);
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
        if (bricks == null || bricks.length == 0) {
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
