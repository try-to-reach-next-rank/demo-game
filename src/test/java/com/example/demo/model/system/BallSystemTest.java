package com.example.demo.model.system;

import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.utils.Vector2D;
import com.example.demo.model.utils.GameVar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BallSystemTest {

    private static final double EPS = 1e-6;

    @Test
    void update_whenBallIsStuck_alignsWithPaddleAndDoesNotApplyMovementStep() {
        // Arrange: create a test-friendly paddle and ball (test constructors assumed present)
        Paddle paddle = new Paddle();
        paddle.setPosition(200.0, 400.0);

        Ball ball = new Ball(paddle);
        // put the ball away from paddle so alignWithPaddle will move it
        ball.setPosition(0.0, 0.0);
        // ensure stuck state
        ball.setStuck(true);

        BallSystem system = new BallSystem(ball, paddle);

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

        Ball ball = new Ball(paddle);
        // release to make it move
        ball.release();
        assertFalse(ball.isStuck());

        // set a simple rightward velocity and known position
        ball.setVelocity(new Vector2D(1.0, 0.0)); // normalized -> (1,0)
        ball.setPosition(10.0, 20.0);

        BallSystem system = new BallSystem(ball, paddle);

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

    @Test
    void resetBall_callsResetState_and_bounceFromPaddle_producesExpectedAngleAtCenterHit() {
        Paddle paddle = new Paddle();
        // place paddle at known coordinates
        paddle.setPosition(100.0, 300.0);

        Ball ball = new Ball(paddle);
        // place ball centered above paddle so hitPos ~ 0.5 and resulting angle should be ~90 degrees
        double centerX = paddle.getX() + paddle.getWidth() / 2.0;
        double ballLeftX = centerX - ball.getWidth() / 2.0;
        ball.setPosition(ballLeftX, paddle.getY() - ball.getHeight());

        BallSystem system = new BallSystem(ball, paddle);

        // mutate ball state and then reset via resetBall
        ball.setVelocity(new Vector2D(0.5, 0.5));
        ball.setAccelerated(true);
        system.resetBall(ball);

        // After resetState the ball should be stuck (resetState sets stuck=true)
        assertTrue(ball.isStuck(), "resetBall should call resetState() and set ball stuck");

        // Now test bounceFromPaddle: release first to allow changing velocity
        // Position ball so center hits center of paddle (we already positioned it)
        system.bounceFromPaddle(paddle);

        Vector2D v = ball.getVelocity();
        // For a center hit (hitPos ~ 0.5) the computed angle in code is 90 degrees,
        // so cos(90deg) ~= 0 and -sin(90deg) ~= -1 -> velocity approx (0, -1)
        assertEquals(0.0, v.x, 1e-3, "Velocity.x should be approximately 0 for center hit");
        assertEquals(-1.0, v.y, 1e-3, "Velocity.y should be approximately -1 for center hit");
    }
}