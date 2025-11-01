package com.example.demo.model.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Brick;
import com.example.demo.model.core.PowerUp;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.GameVar;
import com.example.demo.utils.Sound;
import com.example.demo.view.graphics.BrickTextureProvider;
import com.example.demo.view.EffectRenderer;

import java.util.List;
import java.util.Random;

/**
 * Handles brick damage, explosion propagation, and power-up spawning.
 */
public class BrickSystem implements Updatable {
    private final Brick[] bricks;
    private final List<PowerUp> powerUps;
    private final Random random = new Random();

    public BrickSystem(Brick[] bricks, List<PowerUp> powerUps) {
        this.bricks = bricks;
        this.powerUps = powerUps;
    }

    @Override
    public void update(double deltaTime) {
        // Bricks are static — no movement updates
    }

    /**
     * Handles a collision between a ball and a brick.
     */
    public void onBallHitBrick(Brick brick, Ball ball) {
        if (brick.isDestroyed()) return;
        if (ball == null) return;
        applyDamage(brick, ball.isStronger());

        // Check destruction and trigger effects
        if (brick.isDestroyed()) {
            spawnDestructionEffect(brick);
            maybeSpawnPowerUp(brick);
        }
    }

    /**
     * Applies damage to a brick and updates its texture.
     */
    public void applyDamage(Brick brick, boolean isStronger) {
        if (brick.getHealth() == Integer.MAX_VALUE) return;
        int power = (isStronger) ? GameVar.MAXPOWER : GameVar.MINPOWER;
        int health = brick.getHealth() - power;
        brick.setHealth(health);

        if (health <= 0) {
            brick.setDestroyed(true);
            // Sound.getInstance().playSound("brick_break"); TODO: maybe add brick break sound
        } else {
            String newImageKey = BrickTextureProvider.getTextureForHealth(health);
            brick.setImageKey(newImageKey);
            Sound.getInstance().playSound("brick_hit");
        }
    }

    /**
     * Spawns a power-up at the destroyed brick's location.
     */
    private void maybeSpawnPowerUp(Brick brick) {
        if (random.nextInt(100) < 99) { // 30% chance
            PowerUp powerUp = new PowerUp(GameVar.powerUps[GameRandom.nextInt(4)]);
            powerUp.dropFrom(brick);
            powerUps.add(powerUp);
        }
    }

    /**
     * Creates a visual explosion effect when a brick is destroyed.
     */
    private void spawnDestructionEffect(Brick brick) {
        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight();
        EffectRenderer.getInstance().spawn("explosion1", centerX, centerY, 0.5);
    }
}
