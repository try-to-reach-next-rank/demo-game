package com.example.demo.model.core;

import com.example.demo.model.utils.GameVar;

public class Paddle extends GameObject {
    private int direction;          // -1 = left, 0 = stop, 1 = right
    private final double speed = 400.0;   // base speed (pixels/second)
    private boolean biggerPaddle;

    public Paddle() {
        super("/images/Paddle.png", GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
        resetState();
    }

    public void resetState() {
        setPosition(GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
        resetScale();
        direction = 0;
        biggerPaddle = false;
    }

    // Accessors and mutators
    public int getDirection() { return direction; }
    public void setDirection(int dir) { this.direction = dir; }
    public double getSpeed() { return speed; }

    public boolean getBiggerPaddle() { return this.biggerPaddle; }
    public void setBiggerPaddle(boolean biggerPaddle){ this.biggerPaddle = biggerPaddle; }

}
