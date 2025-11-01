package com.example.demo.model.system;

import com.example.demo.model.core.Paddle;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class PaddleSystemTest {

    private static final double EPS = 1e-6;

    @Test
    void update_movesAccordingToDirectionAndSpeed_and_respectsDeltaTime() {
        // Arrange
        Paddle paddle = new Paddle();
        paddle.setPosition(100.0, 300.0);
        paddle.setDirection(1);// move right


        PaddleSystem system = new PaddleSystem(paddle);

        // Act: half a second
        system.update(0.5);

        // Assert
        double expectedX = 100.0 + GameVar.BASE_SPEED_PADDLE * 0.5;
        assertEquals(expectedX, paddle.getX(), EPS);
    }

    @Test
    void update_clampsToLeftBoundary_whenMovementGoesBeyond() {
        // Arrange
        Paddle paddle = new Paddle();
        // start slightly left of left boundary
        paddle.setPosition(GameVar.WIDTH_OF_WALLS - 5.0, GameVar.INIT_PADDLE_Y);
        paddle.setDirection(-1); // move left

        PaddleSystem system = new PaddleSystem(paddle);

        // Act
        system.update(1.0);

        // Assert: should be clamped to left boundary
        double expected = GameVar.WIDTH_OF_WALLS;
        assertEquals(expected, paddle.getX(), EPS);
    }


    @Test
    void update_clampsToRightBoundary_whenMovementGoesBeyond() {
        // Arrange
        Paddle paddle = new Paddle();
        double rightLimit = GlobalVar.WIDTH - GameVar.WIDTH_OF_WALLS;
        // start slightly right of right boundary
        paddle.setPosition(rightLimit + 5.0, GameVar.INIT_PADDLE_Y);
        paddle.setDirection(1); // move right

        PaddleSystem system = new PaddleSystem(paddle);

        // Act
        system.update(1.0);

        // Assert: should be clamped to right boundary
        double expected = rightLimit - paddle.getWidth();
        assertEquals(expected, paddle.getX(), EPS);
    }


    @Test
    void reset_callsPaddleResetState() {
        // Arrange
        AtomicBoolean resetCalled = new AtomicBoolean(false);

        Paddle paddle = new Paddle() {
            @Override
            public void resetState() {
                resetCalled.set(true); // đánh dấu là hàm được gọi
            }
        };

        PaddleSystem system = new PaddleSystem(paddle);
        system.reset();
        assertTrue(resetCalled.get(), "PaddleSystem.reset() should call paddle.resetState()");
    }

}