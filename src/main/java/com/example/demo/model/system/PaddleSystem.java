package com.example.demo.model.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.GlobalVar;

public class PaddleSystem implements Updatable {
    private final Paddle paddle;

    public PaddleSystem(Paddle paddle) {
        this.paddle = paddle;
    }

    @Override
    public void update(double deltaTime) {

        if (paddle.getBiggerPaddle()) paddle.setScale(2, 2);
        else paddle.resetScale();

        double newX = paddle.getX() + paddle.getDirection() * paddle.getSpeed() * deltaTime;

        // Clamp inside screen bounds
        if (newX < GameVar.HEIGHT_OF_WALLS) {
            newX = GameVar.HEIGHT_OF_WALLS;
        } else if (newX > GlobalVar.WIDTH - paddle.getWidth() - GameVar.WIDTH_OF_WALLS) {
            newX = GlobalVar.WIDTH - paddle.getWidth() - GameVar.WIDTH_OF_WALLS;
        }

        paddle.setPosition(newX, paddle.getY());
    }

    public void reset() {
        paddle.resetState();
    }
}
