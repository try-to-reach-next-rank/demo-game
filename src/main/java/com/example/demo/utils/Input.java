package com.example.demo.utils;

import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class Input {
    private final Paddle paddle;
    private final Ball ball;

    private KeyCode lastPressed = null;
    private boolean leftHeld = false;
    private boolean rightHeld = false;

    public Input(Paddle paddle, Ball ball) {
        this.paddle = paddle;
        this.ball = ball;
    }

    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.LEFT) {
            leftHeld = true;
            lastPressed = KeyCode.LEFT;
            paddle.setDirection(-1);
        } else if (code == KeyCode.RIGHT) {
            rightHeld = true;
            lastPressed = KeyCode.RIGHT;
            paddle.setDirection(1);
        } else if (code == KeyCode.SPACE && ball.isStuck()) {
            ball.release();
        }
    }

    public void handleKeyReleased(KeyCode code) {
        if (code == KeyCode.LEFT) leftHeld = false;
        else if (code == KeyCode.RIGHT) rightHeld = false;

        if (code == lastPressed) {
            // Revert to the other key if it’s still held
            if (leftHeld && !rightHeld) {
                lastPressed = KeyCode.LEFT;
                paddle.setDirection(-1);
            } else if (rightHeld && !leftHeld) {
                lastPressed = KeyCode.RIGHT;
                paddle.setDirection(1);
            } else {
                lastPressed = null;
                paddle.setDirection(0);
            }
        }
    }
}
