package com.example.demo.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.PowerUp;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.utils.GameRandom;
import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.Sound;
import com.example.demo.view.graphics.BrickTextureProvider;
import com.example.demo.view.EffectRenderer;
import javafx.scene.image.Image;

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
    public void onBallHitBrick(Ball ball, Brick brick) {
        if (brick.isDestroyed()) return;

        applyDamage(brick);

        // Check destruction and trigger effects
        if (brick.isDestroyed()) {
            spawnDestructionEffect(brick);
            maybeSpawnPowerUp(brick);
        }
    }

    /**
     * Applies damage to a brick and updates its texture.
     */
    public void applyDamage(Brick brick) {
        int health = brick.getHealth() - 1;
        brick.setHealth(health);

        if (health <= 0) {
            brick.setDestroyed(true);
//            Sound.getInstance().playSound("brick_break"); TODO: maybe add brick break sound
        } else {
            Image newTexture = BrickTextureProvider.getTextureForHealth(health);
            brick.setImage(newTexture);
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
        double centerY = brick.getY() + brick.getHeight() / 2;
        EffectRenderer.getInstance().spawnEffect("explosion", centerX, centerY, 0.3);
    }

    /**
     * Applies explosion logic from a source brick.
     */
    public void handleExplosion(Brick sourceBrick) {
        double cx = sourceBrick.getX() + sourceBrick.getWidth() / 2;
        double cy = sourceBrick.getY() + sourceBrick.getHeight() / 2;
        double radius = sourceBrick.getWidth() * 2.5;

        for (Brick other : bricks) {
            if (other == sourceBrick || other.isDestroyed()) continue;

            double ocx = other.getX() + other.getWidth() / 2;
            double ocy = other.getY() + other.getHeight() / 2;
            double distance = Math.sqrt(Math.pow(cx - ocx, 2) + Math.pow(cy - ocy, 2));

            if (distance <= radius) {
                applyDamage(other);
            }
        }
    }
}
