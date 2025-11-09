package com.example.demo.controller;

import com.example.demo.controller.core.CollisionController;
import com.example.demo.model.core.*;
import com.example.demo.model.core.Brick;
import com.example.demo.controller.system.BallSystem;
import com.example.demo.controller.system.BrickSystem;
import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.utils.Vector2D;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CollisionController.
 *
 * Strategy:
 * - Construct a CollisionController with a null GameWorld (we call its private handlers directly via reflection).
 * - Provide lightweight "spy" subclasses of BallSystem, BrickSystem and PowerUpSystem to observe delegations.
 * - Use real Ball / Paddle / Brick / PowerUp where possible so tests follow the style of existing tests.
 *
 * These tests exercise:
 * - handleBallFloorCollision -> delegates resetBall on BallSystem
 * - handlePaddlePowerUpCollisions -> delegates activate on PowerUpSystem and hides visible power-up; also hides when falls below bottom
 * - handleBallPaddleCollision -> delegates bounceFromPaddle on BallSystem when bounds intersect and ball not stuck
 * - handleBallBrickCollisions -> delegates onBallHitBrick and applies bounce on the correct axis (side bounce flips x)
 *
 * Note: tests avoid asserting static side-effects (Sound, EffectRenderer) and focus on logical delegation / state changes.
 */
class CollisionControllerTest {

    // Utility to invoke private methods on CollisionController
    private void invokePrivate(CollisionController cm, String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method m = CollisionController.class.getDeclaredMethod(methodName, paramTypes);
        m.setAccessible(true);
        m.invoke(cm, args);
    }

    @Test
    void handleBallFloorCollision_callsResetBall_whenBallBelowBottom() throws Exception {
        // Arrange: create paddle/ball for constructing spy BallSystem
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);
        // place ball below bottom edge
        ball.setPosition(0.0, GlobalVar.BOTTOM_EDGE + 10.0);

        AtomicBoolean resetCalled = new AtomicBoolean(false);

        BallSystem spyBallSystem = new BallSystem(ball, paddle) {
            @Override
            public void resetBall(Ball b) {
                resetCalled.set(true);
                // don't call super to avoid changing other state
            }
        };

        BrickSystem brickSystem = new BrickSystem(new Brick[0], new ArrayList<>());
        PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, new ArrayList<>());

        CollisionController cm = new CollisionController(null, spyBallSystem, brickSystem, powerUpSystem);

        // Act: invoke private handler directly
        invokePrivate(cm, "handleBallFloorCollision", new Class[]{Ball.class}, ball);

        // Assert
        assertTrue(resetCalled.get(), "resetBall should be called when ball falls below bottom edge");
    }

    @Test
    void handlePaddlePowerUpCollisions_activatesPowerUp_and_hidesIt_whenIntersectingPaddle() throws Exception {
        // Arrange
        Paddle paddle = new Paddle();
        paddle.setWidth(40);
        paddle.setHeight(40);
        paddle.setPosition(100.0, 300.0);
        // create a visible power-up positioned to intersect the paddle
        PowerUp powerUp = new PowerUp(GameVar.ACCELERATE);
        powerUp.setWidth(40);
        powerUp.setHeight(40);

        powerUp.setPosition(paddle.getX() + 1.0, paddle.getY()); // overlap
        powerUp.setVisible(true);

        List<PowerUp> worldPowerUps = new ArrayList<>();
        worldPowerUps.add(powerUp);

        Ball ball = new Ball(paddle);

        AtomicBoolean activated = new AtomicBoolean(false);
        PowerUpSystem spyPowerUpSystem = new PowerUpSystem(ball, paddle, worldPowerUps) {
            @Override
            public void activate(PowerUp p) {
                activated.set(true);
                // do not call super to keep test deterministic
            }
        };

        BallSystem ballSystem = new BallSystem(ball, paddle);
        BrickSystem brickSystem = new BrickSystem(new Brick[0], new ArrayList<>());

        CollisionController cm = new CollisionController(null, ballSystem, brickSystem, spyPowerUpSystem);

        // Pre-check
        assertTrue(powerUp.isVisible());

        // Act
        invokePrivate(cm, "handlePaddlePowerUpCollisions", new Class[]{Paddle.class, List.class}, paddle, worldPowerUps);

        // Assert: activate called and power-up hidden
        assertTrue(activated.get(), "PowerUpSystem.activate should be called when paddle intersects a visible power-up");
        assertFalse(powerUp.isVisible(), "Power-up should be hidden (not visible) after collection");
    }

    @Test
    void handlePaddlePowerUpCollisions_hidesPowerUp_whenFallsBelowBottomEdge() throws Exception {
        // Arrange
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);

        PowerUp powerUp = new PowerUp(GameVar.ACCELERATE);
        powerUp.setPosition(10.0, GlobalVar.BOTTOM_EDGE + 50.0); // below bottom
        powerUp.setVisible(true);

        List<PowerUp> worldPowerUps = new ArrayList<>();
        worldPowerUps.add(powerUp);

        PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, worldPowerUps);
        BallSystem ballSystem = new BallSystem(ball, paddle);
        BrickSystem brickSystem = new BrickSystem(new Brick[0], new ArrayList<>());

        CollisionController cm = new CollisionController(null, ballSystem, brickSystem, powerUpSystem);

        // Act
        invokePrivate(cm, "handlePaddlePowerUpCollisions", new Class[]{Paddle.class, List.class}, paddle, worldPowerUps);

        // Assert
        assertFalse(powerUp.isVisible(), "Power-up falling below bottom edge should be hidden");
    }

    @Test
    void handleBallPaddleCollision_callsBounceFromPaddle_whenIntersect_and_notStuck() throws Exception {
        // Arrange
        Paddle paddle = new Paddle();
        paddle.setWidth(30);
        paddle.setPosition(GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);

        Ball ball = new Ball(paddle);
        // place ball so bounds intersect paddle
        ball.setPosition(paddle.getX() + 1.0, paddle.getY());

        AtomicBoolean bounced = new AtomicBoolean(false);
        BallSystem spyBallSystem = new BallSystem(ball, paddle) {
            @Override
            public void bounceFromPaddle(Paddle p) {
                bounced.set(true);
            }
        };

        BrickSystem brickSystem = new BrickSystem(new Brick[0], new ArrayList<>());
        PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, new ArrayList<>());

        CollisionController cm = new CollisionController(null, spyBallSystem, brickSystem, powerUpSystem);

        // Ensure ball is not stuck so sound/cooldown branch can run (we don't assert sound)
        ball.setStuck(false);

        // Act
        invokePrivate(cm, "handleBallPaddleCollision", new Class[]{Ball.class, Paddle.class}, ball, paddle);

        // Assert
        assertTrue(bounced.get(), "bounceFromPaddle should be called when ball intersects paddle and is not stuck");
    }

    @Test
    void handleBallBrickCollisions_delegatesToBrickSystem_and_appliesSideBounce() throws Exception {
        // Arrange paddle/ball for constructors
        Paddle paddle = new Paddle();
        paddle.setWidth(40);
        paddle.setHeight(40);

        Ball ball = new Ball(paddle);
        ball.setWidth(40);
        ball.setHeight(40);
        ball.setStronger(false); // ensure not skipped

        // Ball moving right
        ball.setPosition(100.0, 100.0);
        ball.setVelocity(new Vector2D(1.0, 0.0));

        // Brick slightly to the right — overlapX < overlapY → side collision
        Brick brick = new Brick(null, 100, 100, 5);
        brick.setPosition(ball.getX() + ball.getWidth() - 5, ball.getY() + 5);
        brick.setWidth(40.0);
        brick.setHeight(40.0);
        brick.setHealth(1);
        brick.setDestroyed(false);

        Brick[] bricks = new Brick[]{brick};

        AtomicBoolean brickHit = new AtomicBoolean(false);
        // Spy: define both overloads so test works regardless of parameter ordering in the real BrickSystem
        BrickSystem spyBrickSystem = new BrickSystem(bricks, new ArrayList<>()) {
            public void onBallHitBrick(Brick br, Ball b) {
                brickHit.set(true);
            }
        };

        BallSystem ballSystem = new BallSystem(ball, paddle);
        PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, new ArrayList<>());

        CollisionController cm = new CollisionController(null, ballSystem, spyBrickSystem, powerUpSystem);

        // Ensure precondition: intersects
        assertTrue(ball.getBounds().intersects(brick.getBounds()), "Ball and brick must intersect before collision");

        double beforeX = ball.getVelocity().x;
        double beforeY = ball.getVelocity().y;

        // Act
        invokePrivate(cm, "handleBallBrickCollisions", new Class[]{Ball.class, Brick[].class}, ball, bricks);

        // Assert
        assertTrue(brickHit.get(), "onBallHitBrick should be called for intersecting brick");

        Vector2D after = ball.getVelocity();
        assertEquals(-beforeX, after.x, 1e-6, "Ball x-velocity should be inverted on side collision");
        assertEquals(beforeY, after.y, 1e-6, "Ball y-velocity should remain unchanged on side collision");
    }

    @Test
    void handleBallBrickCollisions_doesNotBounce_whenBallIsStronger() throws Exception {
        // Arrange
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);

        ball.setPosition(100.0, 100.0);
        ball.setVelocity(new Vector2D(1.0, 0.0));
        ball.setStronger(true); // stronger -> should pass through without bounce

        Brick brick = new Brick(null, 100, 100, 5);
        brick.setPosition(ball.getX(), ball.getY());
        brick.setWidth(20.0);
        brick.setHeight(20.0);
        brick.setHealth(1);
        brick.setDestroyed(false);

        Brick[] bricks = new Brick[]{brick};

        AtomicBoolean brickHit = new AtomicBoolean(false);
        BrickSystem spyBrickSystem = new BrickSystem(bricks, new ArrayList<>()) {
            @Override
            public void onBallHitBrick(Brick br, Ball b) {
                brickHit.set(true);
            }
        };

        BallSystem ballSystem = new BallSystem(ball, paddle);
        PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, new ArrayList<>());

        CollisionController cm = new CollisionController(null, ballSystem, spyBrickSystem, powerUpSystem);

        double beforeX = ball.getVelocity().x;
        double beforeY = ball.getVelocity().y;

        // Act
        invokePrivate(cm, "handleBallBrickCollisions", new Class[]{Ball.class, Brick[].class}, ball, bricks);

        // Assert
        assertTrue(brickHit.get(), "onBallHitBrick should be called even when ball is stronger");
        Vector2D after = ball.getVelocity();
        assertEquals(beforeX, after.x, 1e-6, "Ball x-velocity should not change when stronger");
        assertEquals(beforeY, after.y, 1e-6, "Ball y-velocity should not change when stronger");
    }
}