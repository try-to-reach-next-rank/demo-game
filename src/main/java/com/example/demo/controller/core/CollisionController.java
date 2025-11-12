package com.example.demo.controller.core;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;
import com.example.demo.model.core.*;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.controller.system.BallSystem;
import com.example.demo.controller.system.BrickSystem;
import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.utils.Sound;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.EffectRenderer;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

import java.util.ArrayList;
import java.util.List;

/**
 * CollisionManager detects collisions and delegates resolution
 * to the appropriate System (BallSystem, BrickSystem, PowerUpSystem).
 *
 * It no longer directly mutates the models.
 */
public class CollisionController implements Updatable {
    private long nextPaddleSoundTime = 0L;

    private final GameWorld world;
    private boolean ballLost = false;
    private final BallSystem ballSystem;
    private final BrickSystem brickSystem;
    private final PowerUpSystem powerUpSystem;
    private final List<PowerUp> toRemove = new ArrayList<>();

    public CollisionController(GameWorld world, BallSystem ballSystem,
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
        if (ball.isStuck()) {
            ballLost = false;
            return;
        }

        boolean belowFloor = ball.getBounds().getMaxY() > GlobalVar.BOTTOM_EDGE;

        if (belowFloor && !ballLost) {
            Sound.getInstance().playSound("game_over");
            ballSystem.resetBall(ball);
            powerUpSystem.reset();
            ballLost = true;
        }

        if (!belowFloor) {
            ballLost = false;
        }
    }

    private void handlePaddlePowerUpCollisions(Paddle paddle, List<PowerUp> powerUps) {
        if (powerUps == null) return;

        List<PowerUp> toRemove = new ArrayList<>();
        for (PowerUp p : powerUps) {
            if (!p.isVisible() || p.hasExpired()) {
                ThePool.PowerUpPool.release(p);
                toRemove.add(p);
                continue;
            }

            if (p.getBounds().intersects(paddle.getBounds())) {
                powerUpSystem.activate(p);
                p.setVisible(false);
                ThePool.PowerUpPool.release(p);
                toRemove.add(p);
            }
        }
        powerUps.removeAll(toRemove);
    }

    private void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!ball.getBounds().intersects(paddle.getBounds())) return;

        if (!ball.isStuck()) {
            long now = System.currentTimeMillis();
            if (now > nextPaddleSoundTime) {
                Sound.getInstance().playSound("paddle_hit");
                nextPaddleSoundTime = now + GameVar.PADDLE_SOUND_COOLDOWN;
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
            EffectRenderer.getInstance().spawn(
                    GameVar.EXPLOSION2_EFFECT_KEY,
                    ball.getX() + ball.getWidth() / 2,
                    ball.getY() + ball.getHeight() / 2,
                    GameVar.EFFECT_DURATION
            );
        }
    }

    private void handleBallBrickCollisions(Ball ball, Brick[] bricks) {
        if (bricks == null) return;

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;
            if (!ball.getBounds().intersects(brick.getBounds())) continue;
            if (ball.getLastBrick() == brick) continue;

            // delegate to BrickSystem to apply damage, explosion, and power-up drop
            brickSystem.onBallHitBrick(brick, ball);
            Sound.getInstance().playSound("brick_hit");

            // bounce depending on overlap direction
            Vector2D v = ball.getVelocity();
            double overlapX = overlapX(ball, brick);
            double overlapY = overlapY(ball, brick);
            boolean fromSide = overlapX <= overlapY;
            if (ball.isStronger()) continue;
            if (fromSide) ball.setVelocity(-v.x, v.y);
            else ball.setVelocity(v.x, -v.y);

            resolveBallBrickOverlap(ball, brick);

            break;
        }

        for (Brick brick : bricks){
            if (ball.getBounds().intersects(brick.getBounds())){
                break;
            }
            ball.setLastBrick(null);
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

    private void resolveBallBrickOverlap(Ball ball, Brick brick) {
        var ballBounds = ball.getBounds();
        var brickBounds = brick.getBounds();
        double overlapX = overlapX(ball, brick);
        double overlapY = overlapY(ball, brick);

        // Push ball out along the smaller overlap axis
        if (overlapX < overlapY) {
            if (ballBounds.getCenterX() < brickBounds.getCenterX())
                ball.setX(ball.getX() - overlapX);
            else
                ball.setX(ball.getX() + overlapX);
        } else {
            if (ballBounds.getCenterY() < brickBounds.getCenterY())
                ball.setY(ball.getY() - overlapY);
            else
                ball.setY(ball.getY() + overlapY);
        }
    }
}
