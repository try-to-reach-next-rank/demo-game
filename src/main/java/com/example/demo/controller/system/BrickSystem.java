package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Brick;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.graphics.BrickTextureProvider;
import com.example.demo.view.EffectRenderer;

import java.util.List;

/**
 * Handles brick damage, explosion propagation, and power-up spawning.
 * TODO: Add handle reveal bricks in here
 */
public class BrickSystem implements Updatable {
    private final List<Brick> bricks;
    private final PowerUpSystem powerUpSystem;

    public BrickSystem(List<Brick> bricks, SystemManager systemManager) {
        this.bricks = bricks;
        this.powerUpSystem = systemManager.get(PowerUpSystem.class);
    }

    @Override
    public void update(double deltaTime) {
        // Bricks are static — no movement updates
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
        int power = (ball.isStronger()) ? GameVar.MAXPOWER : GameVar.MINPOWER;
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
     * Creates a visual explosion effect when a brick is destroyed.
     */
    private void spawnDestructionEffect(Brick brick) {
        double centerX = brick.getX() + brick.getWidth() / 2;
        double centerY = brick.getY() + brick.getHeight();
        EffectRenderer.getInstance().spawn(GameVar.EXPLOSION1_EFFECT_KEY, centerX, centerY, GameVar.EFFECT_DURATION);
    }
}
