package com.example.demo.model.core;

import com.example.demo.model.utils.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BallTest {

    private Paddle paddle;
    private Ball ball;

    @BeforeEach
    void setUp() {
        paddle = new Paddle();
        ball = new Ball(paddle);
    }

    @Test
    void testInitialStateAndReset() {
        // Constructor calls resetState(), so ball should start stuck with normalized velocity
        assertTrue(ball.isStuck(), "Ball should be stuck after initialization/reset");
        Vector2D vel = ball.getVelocity();
        assertNotNull(vel, "Velocity should not be null after reset");

        double magnitude = Math.hypot(vel.x, vel.y);
        assertEquals(1.0, magnitude, 1e-6, "Velocity should be normalized (magnitude == 1)");
    }

    @Test
    void testReleaseSetsStuckFalseAndVelocity() {
        // Ensure release changes stuck flag and sets velocity to (0, -1)
        assertTrue(ball.isStuck());
        ball.release();
        assertFalse(ball.isStuck(), "Ball should not be stuck after release");

        Vector2D vel = ball.getVelocity();
        assertNotNull(vel);
        assertEquals(0.0, vel.x, 1e-6, "After release, velocity.x should be 0");
        assertEquals(-1.0, vel.y, 1e-6, "After release, velocity.y should be -1");
    }

    @Test
    void testAlignWithPaddlePositionsBallCorrectly() {
        // Place paddle at a known position and align ball with it
        double offsetY = 10.0;
        paddle.setPosition(200.0, 400.0);

        // call alignWithPaddle with lerpFactor = 1.0 so it snaps to target
        ball.alignWithPaddle(offsetY, 1.0);

        double expectedX = paddle.getX() + paddle.getWidth() / 2.0 - ball.getWidth() / 2.0;
        double expectedY = paddle.getY() - ball.getHeight() - offsetY;

        assertEquals(expectedX, ball.getX(), 1e-6, "Ball X should be centered on paddle");
        assertEquals(expectedY, ball.getY(), 1e-6, "Ball Y should be offset above the paddle by offsetY");
    }
}