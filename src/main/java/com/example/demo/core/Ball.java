package com.example.demo.core;

public class Ball extends GameObject {
    private int xdir, ydir;
    private boolean isAccelerated;
    private boolean stuck;
    private Paddle paddle;

    public Ball(Paddle paddle) {
        super("/images/Ball.png", VARIABLES.INIT_BALL_X, VARIABLES.INIT_BALL_Y);
        this.paddle = paddle;
        initBall();
    }

    private void initBall() {
        xdir = 1;
        ydir = -1;
        isAccelerated = false;
        stuck = true;
        resetState();
    }

    public void move() {
        if(stuck) { // make the ball float on top of paddle until launch
            setPosition(paddle.getX() + paddle.getWidth() / 2.0 - getWidth() / 2.0,
                        paddle.getY() - getHeight());
            return;
        }

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

        if(y >= VARIABLES.HEIGHT) {
            stuck = true;
            resetState();
        }

        setPosition(x, y);
    }

    public void resetState() {
        setPosition(paddle.getX() + paddle.getWidth() / 2 - getWidth() / 2,
                    paddle.getY() - getHeight());
        stuck = true;
        ydir = -1;
    }

    public void release() {
        stuck = false;
    }

    // Thêm phương thức để bật trạng thái tăng tốc
    public void setAccelerated(boolean val) {
        this.isAccelerated = val;
    }

    public void setXDir(int x) { xdir = x; }
    public void setYDir(int y) { ydir = y; }
    public int getYDir() { return ydir; }
}