package com.example.demo.core;

public class Ball extends GameObject {
    private int xdir, ydir;

    public Ball() {
        super("/images/Ball.png", VARIABLES.INIT_BALL_X, VARIABLES.INIT_BALL_Y);
        initBall();
    }

    private void initBall() {
        xdir = 1;
        ydir = -1;
        resetState();
    }

    public void move() {
        x += xdir * VARIABLES.SPEED;
        y += ydir * VARIABLES.SPEED;

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

    public void setXDir(int x) { xdir = x; }
    public void setYDir(int y) { ydir = y; }
    public int getYDir() { return ydir; }
}
