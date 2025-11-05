package com.example.demo.model.core;

import org.junit.jupiter.api.Test;

import com.example.demo.model.core.entities.Paddle;
import com.example.demo.utils.var.GameVar;

import static org.junit.jupiter.api.Assertions.*;

class PaddleTest {

    @Test
    void initialStateAfterConstruction() {
        // Use test-friendly constructor to avoid JavaFX
        Paddle paddle = new Paddle();

        // resetState called in constructor -> position should be the GameVar initial values
        assertEquals(GameVar.INIT_PADDLE_X, paddle.getX(), 1e-9);
        assertEquals(GameVar.INIT_PADDLE_Y, paddle.getY(), 1e-9);

        assertEquals(0, paddle.getDirection());
        assertFalse(paddle.getBiggerPaddle());

        assertEquals(400.0, paddle.getSpeed(), 1e-9);
    }

    @Test
    void setAndGetDirection() {
        Paddle paddle = new Paddle();
        paddle.setDirection(1);
        assertEquals(1, paddle.getDirection());

        paddle.setDirection(-1);
        assertEquals(-1, paddle.getDirection());
    }

    @Test
    void setAndGetBiggerPaddle() {
        Paddle paddle = new Paddle();
        assertFalse(paddle.getBiggerPaddle());

        paddle.setBiggerPaddle(true);
        assertTrue(paddle.getBiggerPaddle());

        paddle.setBiggerPaddle(false);
        assertFalse(paddle.getBiggerPaddle());
    }

    @Test
    void resetStateResetsScaleAndDirection() {
        Paddle paddle = new Paddle();

        // mutate
        paddle.setDirection(1);
        paddle.setBiggerPaddle(true);
        paddle.setPosition(999.0, 999.0);
        paddle.setScale(1.5, 1.5);

        // reset
        paddle.resetState();

        assertEquals(GameVar.INIT_PADDLE_X, paddle.getX(), 1e-9);
        assertEquals(GameVar.INIT_PADDLE_Y, paddle.getY(), 1e-9);
        assertEquals(0, paddle.getDirection());
        assertFalse(paddle.getBiggerPaddle());

        // width/height should equal base width/height after resetScale()
        assertEquals(32, paddle.getWidth(), 1e-9);
        assertEquals(9, paddle.getHeight(), 1e-9);
    }
}