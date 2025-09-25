package com.example.demo.core;

public class Ball extends GameObject {
    private int xdir, ydir;
    private boolean isAccelerated;

    public Ball() {
        super("/images/Ball.png", VARIABLES.INIT_BALL_X, VARIABLES.INIT_BALL_Y);
        initBall();
    }

    private void initBall() {
        xdir = 1;
        ydir = -1;
        isAccelerated = false;
        resetState();
    }

    public void move() {

        float currentSpeed = isAccelerated ? VARIABLES.SPEED * VARIABLES.ACCELERATED_SPEED_MULTIPLIER : VARIABLES.SPEED;
        x += xdir * currentSpeed;
        y += ydir * currentSpeed;

        if (x <= 0) {
            setXDir(1);
        }
        if (x >= VARIABLES.WIDTH - getWidth()) {
            setXDir(-1);
        }
        if (y <= 0) {
            setYDir(1);
        }

        setPosition(x, y);
    }

    private void resetState() {
        setPosition(VARIABLES.INIT_BALL_X, VARIABLES.INIT_BALL_Y);
    }

    // Thêm phương thức để bật trạng thái tăng tốc
    public void setAccelerated(boolean val) {
        this.isAccelerated = val;
    }

    public void setXDir(int x) { xdir = x; }
    public void setYDir(int y) { ydir = y; }
    public int getYDir() { return ydir; }
}