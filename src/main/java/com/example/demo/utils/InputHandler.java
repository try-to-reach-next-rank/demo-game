package com.example.demo.utils;

import com.example.demo.core.Ball;
import com.example.demo.core.Paddle;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {

    private final Paddle paddle;
    private final Ball ball;

    public InputHandler(Paddle paddle, Ball ball) {
        this.paddle = paddle;
        this.ball = ball;
    }

    public void input(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                paddle.setDx(-4);
            } else if (e.getCode() == KeyCode.RIGHT) {
                paddle.setDx(4);
            } else if (e.getCode() == KeyCode.SPACE) {
                ball.release();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                paddle.setDx(0);
            }
        });
    }
}
