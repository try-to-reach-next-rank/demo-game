package com.example.demo.core;

public class Paddle extends GameObject {
    private int direction;          // -1 = left, 0 = stop, 1 = right
    private double speed = 400.0;   // pixels per second (tweakable)

    public Paddle() {
        super("/images/Paddle.png", VARIABLES.INIT_PADDLE_X, VARIABLES.INIT_PADDLE_Y);
        resetState();
    }

    public void update(double deltaTime) {
        // Move according to direction and speed
        x += direction * speed * deltaTime;

        // Clamp inside screen
        if (x < 0) x = 0;
        if (x > VARIABLES.WIDTH - getWidth()) {
            x = VARIABLES.WIDTH - getWidth();
        }

        setPosition(x, y);
    }

    public void resetState() {
        setPosition(VARIABLES.INIT_PADDLE_X, VARIABLES.INIT_PADDLE_Y);
    }

    /** Set paddle movement direction: -1 = left, 1 = right, 0 = stop */
    public void setDirection(int dir) {
        this.direction = dir;
    }
}
