package com.example.demo.model.core;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.PaddleData;
import com.example.demo.utils.var.GameVar;

public class Paddle extends ImageObject<PaddleData> {

    // ===================== Fields =====================
    private int direction;                    // -1 = left, 0 = stop, 1 = right
    private final double speed = GameVar.BASE_SPEED_PADDLE;
    private boolean biggerPaddle;

    // ===================== Constructor =====================
    public Paddle() {
        super("paddle", GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
        resetState();
    }

    // ===================== State Control =====================
    public void resetState() {
        setPosition(GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
        resetScale();
        direction = 0;
        biggerPaddle = false;
    }

    @Override
    public void applyState(PaddleData data) {
        if (data == null) return;
        this.setPosition(data.getX(), data.getY());
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    // ===================== Getters / Setters =====================
    public int getDirection() { return direction; }
    public void setDirection(int dir) { this.direction = dir; }

    public double getSpeed() { return speed; }

    public boolean getBiggerPaddle() { return biggerPaddle; }
    public void setBiggerPaddle(boolean biggerPaddle) { this.biggerPaddle = biggerPaddle; }
}
