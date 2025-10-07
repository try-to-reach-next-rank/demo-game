package com.example.demo.model.core;

import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.GlobalVar;

public class Paddle extends GameObject {
    private int direction;          // -1 = left, 0 = stop, 1 = right
    private double speed = 400.0;   // pixels per second (tweakable)

    public Paddle() {
        super("/images/Paddle.png", GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
        resetState();
    }

    public void update(double deltaTime) {
        // Move according to direction and speed
        x += direction * speed * deltaTime;

        // Clamp inside screen
        if (x < GameVar.HEIGHT_OF_WALLS) x = GameVar.HEIGHT_OF_WALLS;
        if (x > GlobalVar.WIDTH - getWidth() - GameVar.WIDTH_OF_WALLS) {
            x = GlobalVar.WIDTH - getWidth() - GameVar.WIDTH_OF_WALLS;
        }

        setPosition(x, y);
    }

    public void resetState() {
        setPosition(GameVar.INIT_PADDLE_X, GameVar.INIT_PADDLE_Y);
    }

    /** Set paddle movement direction: -1 = left, 1 = right, 0 = stop */
    public void setDirection(int dir) {
        this.direction = dir;
    }
}
