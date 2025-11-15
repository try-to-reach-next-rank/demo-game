package com.example.demo.model.core.entities;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.PaddleData;
import com.example.demo.utils.var.GameVar;

public class Paddle extends ImageObject<PaddleData> {
    private int direction;                                   // -1 = left, 0 = stop, 1 = right
    private final double speed = GameVar.BASE_SPEED_PADDLE;  // base speed (pixels/second)
    // TODO: REMOVE THIS FOR PADDLE, ADD IN POWERUP
    private boolean biggerPaddle;

    public Paddle() {
        super(GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
        setImageKey("paddle");
        resetState();
    }

    public void resetState() {
        setPosition(GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
        resetScale();
        direction = 0;
        biggerPaddle = false;
    }

    @Override
    public void applyState(PaddleData data) {
        super.applyState(data);
    }

    // Accessors and mutators
    public int getDirection() { return direction; }
    public void setDirection(int dir) { this.direction = dir; }
    public double getSpeed() { return speed; }

    public boolean getBiggerPaddle() { return this.biggerPaddle; }
    public void setBiggerPaddle(boolean biggerPaddle) { this.biggerPaddle = biggerPaddle; }
}
