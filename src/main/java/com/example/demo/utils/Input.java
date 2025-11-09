package com.example.demo.utils;

import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;

import javafx.scene.input.KeyCode;

public class Input {
    private final Paddle paddle;
    private final Ball ball;

    private boolean leftHeld = false;
    private boolean rightHeld = false;
    private KeyCode lastPressed = null;

    public Input(Paddle paddle, Ball ball) {
        this.paddle = paddle;
        this.ball = ball;
    }

    public void handleKeyPressed(KeyCode code) {
        switch (code) {
            case LEFT -> {
                leftHeld = true;
                lastPressed = KeyCode.LEFT;
            }
            case RIGHT -> {
                rightHeld = true;
                lastPressed = KeyCode.RIGHT;
            }
            case SPACE -> {
                if (ball.isStuck()) ball.release();
            }
            default -> {
                break;
            }
        }
        updatePaddleDirection();
    }

    public void handleKeyReleased(KeyCode code) {
        switch (code) {
            case LEFT -> leftHeld = false;
            case RIGHT -> rightHeld = false;
            default -> {}
        }

        if (code == lastPressed) {
            if (leftHeld && !rightHeld) lastPressed = KeyCode.LEFT;
            else if (rightHeld && !leftHeld) lastPressed = KeyCode.RIGHT;
            else lastPressed = null;
        }

        updatePaddleDirection();
    }

    private void updatePaddleDirection() {
        if (lastPressed == KeyCode.LEFT && leftHeld) {
            paddle.setDirection(-1);
        } else if (lastPressed == KeyCode.RIGHT && rightHeld) {
            paddle.setDirection(1);
        } else if (leftHeld) {
            paddle.setDirection(-1);
        } else if (rightHeld) {
            paddle.setDirection(1);
        } else {
            paddle.setDirection(0);
        }
    }

    public void update() {
        // Always keep the paddle moving according to held keys and lastPressed
        updatePaddleDirection();
    }
}