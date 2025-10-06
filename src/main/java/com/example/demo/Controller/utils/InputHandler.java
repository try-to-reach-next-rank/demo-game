package com.example.demo.Controller.utils;

import com.example.demo.Controller.core.Ball;
import com.example.demo.Controller.core.Paddle;
import javafx.scene.input.KeyCode;

public record InputHandler(Paddle paddle, Ball ball) {

    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.LEFT) {
            paddle.setDirection(-1);
        } else if (code == KeyCode.RIGHT) {
            paddle.setDirection(1);
        } else if (code == KeyCode.SPACE) {
            if (ball.isStuck()) {
                ball.release();
            }
        }
    }

    public void handleKeyReleased(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.RIGHT) {
            paddle.setDirection(0);
        }
    }
}
