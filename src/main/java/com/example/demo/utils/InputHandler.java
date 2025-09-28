package com.example.demo.utils;

import com.example.demo.core.Ball;
import com.example.demo.core.Paddle;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public record InputHandler(Paddle paddle, Ball ball) {

    public void input(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                paddle.setDirection(-1);
            } else if (e.getCode() == KeyCode.RIGHT) {
                paddle.setDirection(1);
            } else if (e.getCode() == KeyCode.SPACE) {
                if (ball.isStuck()) {
                    ball.release();
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                paddle.setDirection(0);
            }
        });
    }
}
