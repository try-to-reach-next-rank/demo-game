package com.example.demo.model.system;

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

/**
 * Handles brick damage, destruction effects, and power-up spawn logic.
 */
public class BrickSystem implements Updatable {

    // ===================== Fields =====================
    private final Brick[] bricks;
    private final List<PowerUp> powerUps;
    private final Random random = new Random();

    // ===================== Constructor =====================
    public BrickSystem(Brick[] bricks, List<PowerUp> powerUps) {
        this.bricks = bricks;
        this.powerUps = powerUps;
    }

    // ===================== Game Loop =====================
    @Override
    public void update(double deltaTime) {
        // Bricks are static — no movement updates needed
    }

    // ===================== Public API =====================
    /**
     * Called when the ball hits a brick.
     */
    public void onBallHitBrick(Brick brick, Ball ball) {
        if (brick == null || ball == null) return;
        if (brick.isDestroyed()) return;

        applyDamage(brick, ball.isStronger());

        if (brick.isDestroyed()) {
            spawnDestructionEffect(brick);
            maybeSpawnPowerUp(brick);
        }
    }

    // ===================== Damage & Texture =====================
    public void applyDamage(Brick brick, boolean strongerBall) {
        if (brick.getHealth() == Integer.MAX_VALUE) return; // Unbreakable brick

        int damage = strongerBall ? GameVar.MAXPOWER : GameVar.MINPOWER;
        int newHealth = brick.getHealth() - damage;
        brick.setHealth(newHealth);

        if (newHealth <= 0) {
            brick.setDestroyed(true);
            // Optional: Sound.getInstance().playSound("brick_break");
        } else {
            brick.setImageKey(BrickTextureProvider.getTextureForHealth(newHealth));
            Sound.getInstance().playSound("brick_hit");
        }
    }

    // ===================== Power-Up Spawn =====================
    private void maybeSpawnPowerUp(Brick brick) {
        if (random.nextInt(100) < GameVar.POWERUP_SPAWN_CHANCE) {
            String type = GameVar.powerUps[GameRandom.nextInt(GameVar.powerUps.length)];
            PowerUp powerUp = new PowerUp(type);
            powerUp.dropFrom(brick);
            powerUps.add(powerUp);
        }
    }

    // ===================== Effects =====================
    private void spawnDestructionEffect(Brick brick) {
        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight();
        EffectRenderer.getInstance().spawn(
                GameVar.EXPLOSION1_EFFECT_KEY,
                centerX,
                centerY,
                GameVar.EFFECT_DURATION
        );
    }
}
