package com.example.demo.model.system;

import com.example.demo.controller.system.BallSystem;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.utils.Vector2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class BallSystemTest {

    private static final double EPS = 1e-6;

    @Test
    void update_whenBallIsStuck_alignsWithPaddleAndDoesNotApplyMovementStep() {
        // Arrange: create a test-friendly paddle and ball (test constructors assumed present)
        Paddle paddle = new Paddle();
        paddle.setPosition(200.0, 400.0);

        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);

        // Put the ball away from paddle so alignWithPaddle will move it
        ball.setPosition(0.0, 0.0);
        // ensure stuck state
        ball.setStuck(true);

        BallSystem system = new BallSystem(List.of(ball));

        // Act
        system.update(0.016); // one frame

        // Assert: still stuck and x moved towards paddle (alignWithPaddle lerp factor 0.1 -> x increases)
        assertTrue(ball.isStuck(), "Ball should remain stuck after update when stuck");
        // ball x should no longer be 0 (moved towards paddle)
        assertTrue(ball.getX() > 0.0, "Ball.x should have been moved by alignWithPaddle");
    }

    @Test
    void update_whenNotStuck_movesAccordingToVelocityAndSpeed_and_accountsForAccelerationFlag() {
        Paddle paddle = new Paddle();
        paddle.setPosition(100.0, 300.0);

        Ball ball = new Ball();
        ball.setStuckPaddle(paddle);

        // Release to make it move
        ball.release();
        assertFalse(ball.isStuck());

        // set a simple rightward velocity and known position
        ball.setVelocity(new Vector2D(1.0, 0.0)); // normalized -> (1,0)
        ball.setPosition(10.0, 20.0);

        BallSystem system = new BallSystem(List.of(ball));

        // Act: one second of update without acceleration
        system.update(1.0);

        // Expected: x increased by baseSpeed * 1.0 (baseSpeed from Ball is 300.0)
        double expectedX = 10.0 + ball.getBaseSpeed() * 1.0;
        assertEquals(expectedX, ball.getX(), EPS);

        // Now test with acceleration flag (1.5x speed)
        ball.setPosition(10.0, 20.0);
        ball.setVelocity(new Vector2D(1.0, 0.0));
        ball.setStuck(false);
        ball.setAccelerated(true);

        system.update(1.0);

        double expectedAcceleratedX = 10.0 + ball.getBaseSpeed() * 1.5 * 1.0;
        assertEquals(expectedAcceleratedX, ball.getX(), EPS);
    }
}