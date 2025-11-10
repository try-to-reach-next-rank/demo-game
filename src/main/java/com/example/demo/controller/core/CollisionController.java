package com.example.demo.controller.core;

import com.example.demo.controller.system.BallSystem;
import com.example.demo.controller.system.BrickSystem;
import com.example.demo.controller.system.PortalSystem;
import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.controller.system.SystemManager;
import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;
import com.example.demo.model.core.ThePool;
import com.example.demo.model.core.entities.*;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.Sound;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;
import com.example.demo.view.EffectRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * CollisionManager detects collisions and delegates resolution
 * to the appropriate System (BallSystem, BrickSystem, PowerUpSystem).
 *
 * It no longer directly mutates the models.
 */
public class CollisionController {
    private final GameWorld world;
    private final List<PowerUp> toRemove = new ArrayList<>();
    private BallSystem ballSystem;
    private BrickSystem brickSystem;
    private PowerUpSystem powerUpSystem;
    private PortalSystem portalSystem;

    public CollisionController(GameWorld world, SystemManager systemManager) {
        this.world = world;
        this.ballSystem = systemManager.get(BallSystem.class);
        this.brickSystem = systemManager.get(BrickSystem.class);
        this.powerUpSystem = systemManager.get(PowerUpSystem.class);
        this.portalSystem = systemManager.get(PortalSystem.class);
    }

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
        if (ball.getBounds().getMaxY() > GameVar.MAP_MAX_Y) {
            Sound.getInstance().playSound("game_over");
            ballSystem.resetBall(ball); // delegate to BallSystem
            powerUpSystem.reset();
        }
    }

    private void handlePaddlePowerUpCollisions(Paddle paddle, List<PowerUp> powerUps) {
        if (powerUps == null) return;
        for (PowerUp p : powerUps) {
            if (!p.isVisible() || p.hasExpired()) {
                ThePool.PowerUpPool.release(p);
                toRemove.add(p);
            }
            if (p.getBounds().intersects(paddle.getBounds())) {
                powerUpSystem.activate(p); // delegate to PowerUpSystem
                p.setVisible(false);
            }
            if (p.getBounds().getMaxY() > GameVar.MAP_MAX_Y) {
                p.setVisible(false);
            }
        }
        powerUps.removeAll(toRemove);
    }

    private void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!ball.getBounds().intersects(paddle.getBounds())) return;

        ballSystem.handleCollision(ball, paddle);
    }

    private void handleBallWallCollisions(Ball ball, List<Wall> walls) {
        if (walls == null) return;

        for (Wall wall : walls) {
            if (!ball.getBounds().intersects(wall.getBounds())) continue;

            ballSystem.handleCollision(ball, wall);
        }
    }

    private void handleBallBrickCollisions(Ball ball, Brick[] bricks) {
        if (bricks == null) return;

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;
            if (!ball.getBounds().intersects(brick.getBounds())) continue;

            // delegate to BrickSystem to apply damage, explosion, and power-up drop
            brickSystem.onBallHitBrick(brick, ball);
            Sound.getInstance().playSound("brick_hit");

            // bounce depending on overlap direction
            ballSystem.handleCollision(ball, brick);

            break;
        }

        for (Brick brick : bricks){
            if (ball.getBounds().intersects(brick.getBounds())){
                break;
            }
            ball.setLastBrick(null);
        }
    }
}
