package com.example.demo.core;

public class Paddle extends GameObject {
    private int dx;

    public Paddle() {
        super("/images/Paddle.png", VARIABLES.INIT_PADDLE_X, VARIABLES.INIT_PADDLE_Y);
        initPaddle();
    }

    private void initPaddle() {
        resetState();
    }

    public void move() {
        x += dx * VARIABLES.SPEED * 0.5F;

        if (x < 0) {
            x = 0;
        }
        if (x > VARIABLES.WIDTH - getWidth()) {
            x = VARIABLES.WIDTH - getWidth();
        }

        setPosition(x, y); // updates ImageView
    }

    private void resetState() {
        setPosition(VARIABLES.INIT_PADDLE_X, VARIABLES.INIT_PADDLE_Y);
    }

    public void setDx(int dx) {
        this.dx = dx;
    }
}
