package com.example.demo.model.system;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Brick;
import com.example.demo.model.core.PowerUp;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.graphics.BrickTextureProvider;
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
        // Bricks are static — no movement updates
    }

    /**
     * Handles a collision between a ball and a brick.
     */
    public void onBallHitBrick(Brick brick, Ball ball) {
        if (brick.isDestroyed()) return;
        if (ball == null) return;

        int healthBefore = brick.getHealth();
        applyDamage(brick, ball.isStronger());

        // Check destruction and trigger effects
        if (brick.isDestroyed() && healthBefore > 0) { // Đảm bảo chỉ trigger 1 lần
            spawnDestructionEffect(brick);
            maybeSpawnPowerUp(brick);
            if (onBrickDestroyed != null) {
                onBrickDestroyed.accept(brick);
            }
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
        if (random.nextInt(100) < GameVar.POWERUP_SPAWN_CHANCE) {
            PowerUp powerUp = new PowerUp(GameVar.powerUps[GameRandom.nextInt(GameVar.powerUps.length)]);
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
        EffectRenderer.getInstance().spawn(GameVar.EXPLOSION1_EFFECT_KEY, centerX, centerY, GameVar.EFFECT_DURATION);
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
