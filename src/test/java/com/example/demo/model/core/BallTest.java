package com.example.demo.model.core;

import org.junit.jupiter.api.Test;

import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

class BallTest {

    @Test
    void initialStateAfterConstruction() {
        // Use test-friendly Paddle constructor (no JavaFX images)
        Paddle paddle = new Paddle();
        // position it somewhere predictable
        paddle.setPosition(100.0, 400.0);

        Ball ball = new Ball(paddle);

        assertTrue(ball.isStuck(), "Ball should start stuck after construction/resetState");
        assertEquals(300.0, ball.getBaseSpeed(), 1e-9, "Base speed should match expected constant");
        assertNotNull(ball.getVelocity(), "Velocity must be initialized");
        assertEquals(0.0, ball.getVelocity().x, 1e-6);
        assertEquals(-1.0, ball.getVelocity().y, 1e-6);
    }

    @Test
    void releaseShouldUnstickAndSetUpwardVelocity() {
        Paddle paddle = new Paddle();
        paddle.setPosition(0.0, 0.0);

        Ball ball = new Ball(paddle);
        assertTrue(ball.isStuck());

        ball.release();
        assertFalse(ball.isStuck(), "Ball.release() should unset stuck flag");
        assertEquals(0.0, ball.getVelocity().x, 1e-6);
        assertEquals(-1.0, ball.getVelocity().y, 1e-6);
    }

    @Test
    void setVelocityWithVectorShouldNormalize() {
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);

        ball.setVelocity(new Vector2D(3, 4)); // magnitude 5 -> normalized (0.6, 0.8)
        assertEquals(0.6, ball.getVelocity().x, 1e-6);
        assertEquals(0.8, ball.getVelocity().y, 1e-6);
    }

    @Test
    void setVelocityWithComponentsShouldAssignDirectly() {
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);

        // ensure velocity object exists and then set components
        ball.setVelocity(0, -1);
        ball.setVelocity(2, 0);
        assertEquals(2.0, ball.getVelocity().x, 1e-6);
        assertEquals(0.0, ball.getVelocity().y, 1e-6);
    }

    @Test
    void alignWithPaddleShouldClampToPaddleBounds() {
        Paddle paddle = new Paddle();
        paddle.setPosition(0.0, 100.0);

        Ball ball = new Ball(paddle);
        ball.setPosition(10000.0, ball.getY()); // đặt ngoài phạm vi paddle để buộc clamp

        ball.alignWithPaddle(10, 0.5); // dùng lerpFactor < 1.0 để giữ lại x hiện tại

        double expectedMaxX = paddle.getX() + paddle.getWidth() - ball.getWidth();
        assertEquals(expectedMaxX, ball.getX(), 1e-6, "Ball.x should be clamped to paddle's maxX");
    }

}