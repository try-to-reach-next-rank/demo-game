package com.example.demo.controller;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;
import com.example.demo.model.core.*;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Sound;
import com.example.demo.model.utils.Vector2D;
import com.example.demo.system.BallSystem;
import com.example.demo.system.BrickSystem;
import com.example.demo.system.PowerUpSystem;
import com.example.demo.view.EffectRenderer;

import java.util.List;

/**
 * CollisionManager detects collisions and delegates resolution
 * to the appropriate System (BallSystem, BrickSystem, PowerUpSystem).
 *
 * It no longer directly mutates the models.
 */
public class CollisionManager implements Updatable {

    private static final long PADDLE_SOUND_COOLDOWN = 150; // ms
    private long nextPaddleSoundTime = 0L;

    private final GameWorld world;
    private final BallSystem ballSystem;
    private final BrickSystem brickSystem;
    private final PowerUpSystem powerUpSystem;

    public CollisionManager(GameWorld world, BallSystem ballSystem,
                            BrickSystem brickSystem, PowerUpSystem powerUpSystem) {
        this.world = world;
        this.ballSystem = ballSystem;
        this.brickSystem = brickSystem;
        this.powerUpSystem = powerUpSystem;
    }

    @Override
    public void update(double deltaTime) {
        Ball ball = world.getBall();
        Paddle paddle = world.getPaddle();
        Brick[] bricks = world.getBricks();
        List<PowerUp> powerUps = world.getPowerUps();
        List<Wall> walls = world.getWalls();

        if (ball == null || paddle == null) return;

        handleBallFloorCollision(ball);
        handlePaddlePowerUpCollisions(paddle, powerUps);
        handleBallPaddleCollision(ball, paddle);
        handleBallWallCollisions(ball, walls);
        handleBallBrickCollisions(ball, bricks);
    }

    // ------------------------------------------------------------------------
    //  Collision Handling Methods
    // ------------------------------------------------------------------------

    private void handleBallFloorCollision(Ball ball) {
        if (ball.getBounds().getMaxY() > GlobalVar.BOTTOM_EDGE) {
            Sound.getInstance().playSound("game_over");
            ballSystem.resetBall(ball); // delegate to BallSystem
        }
    }

    private void handlePaddlePowerUpCollisions(Paddle paddle, List<PowerUp> powerUps) {
        if (powerUps == null) return;

        for (PowerUp p : powerUps) {
            if (!p.isVisible()) continue;
            if (p.getBounds().intersects(paddle.getBounds())) {
                powerUpSystem.activate(p); // delegate to PowerUpSystem
                p.setVisible(false);
            }
            if (p.getBounds().getMaxY() > GlobalVar.BOTTOM_EDGE) {
                p.setVisible(false);
            }
        }
    }

    private void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!ball.getBounds().intersects(paddle.getBounds())) return;

        if (!ball.isStuck()) {
            long now = System.currentTimeMillis();
            if (now > nextPaddleSoundTime) {
                Sound.getInstance().playSound("paddle_hit");
                nextPaddleSoundTime = now + PADDLE_SOUND_COOLDOWN;
            }
        }

        ballSystem.bounceFromPaddle(paddle); // delegate to BallSystem
    }

    private void handleBallWallCollisions(Ball ball, List<Wall> walls) {
        if (walls == null) return;

        for (Wall wall : walls) {
            if (!ball.getBounds().intersects(wall.getBounds())) continue;

            ballSystem.bounceFromWall(ball, wall); // delegate to BallSystem
            Sound.getInstance().playSound("wall_hit");

            // simple effect (View layer responsibility)
            EffectRenderer.getInstance().spawnEffect(
                    "explosion2",
                    ball.getX() + ball.getWidth() / 2,
                    ball.getY() + ball.getHeight() / 2,
                    0.3
            );
            System.out.printf("Wall %s: (%.1f, %.1f, %.1f, %.1f)%n",
                    wall.getSide(), wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
        }
    }

    private void handleBallBrickCollisions(Ball ball, Brick[] bricks) {
        if (bricks == null) return;

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;
            if (!ball.getBounds().intersects(brick.getBounds())) continue;

            // delegate to BrickSystem to apply damage, explosion, and power-up drop
            brickSystem.onBallHitBrick(ball, brick);

            // bounce depending on overlap direction
            Vector2D v = ball.getVelocity();
            double overlapX = overlapX(ball, brick);
            double overlapY = overlapY(ball, brick);
            boolean fromSide = overlapX < overlapY;
            if (ball.isStronger()) continue;
            if (fromSide) ball.setVelocity(new Vector2D(-v.x, v.y));
            else ball.setVelocity(new Vector2D(v.x, -v.y));
        }
    }

    // ------------------------------------------------------------------------
    //  Small geometry helpers
    // ------------------------------------------------------------------------

    private double overlapX(GameObject a, GameObject b) {
        return Math.min(a.getBounds().getMaxX(), b.getBounds().getMaxX()) -
                Math.max(a.getBounds().getMinX(), b.getBounds().getMinX());
    }

    private double overlapY(GameObject a, GameObject b) {
        return Math.min(a.getBounds().getMaxY(), b.getBounds().getMaxY()) -
                Math.max(a.getBounds().getMinY(), b.getBounds().getMinY());
    }
}
