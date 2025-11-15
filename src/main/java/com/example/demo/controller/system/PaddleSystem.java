package com.example.demo.controller.system;

import java.util.List;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.entities.Wall;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

public class PaddleSystem implements Updatable {
    private final List<Paddle> paddles;

    public PaddleSystem(List<Paddle> paddles) {
        this.paddles = paddles;
    }

    @Override
    public void update(double deltaTime) {
        for (Paddle paddle : paddles) {
            // Bigger paddle
            if (paddle.getBiggerPaddle()) paddle.setScale(GameVar.PADDLE_BIGGER_SCALE_X, GameVar.PADDLE_BIGGER_SCALE_Y);
            else paddle.resetScale();

            double newX = paddle.getX() + paddle.getDirection() * paddle.getSpeed() * deltaTime;

            // Clamp inside screen bounds
            if (newX < GameVar.MAP_MIN_X) {
                newX = GameVar.MAP_MIN_X + GameVar.PADDLE_OFFSET_X_SCREEN;
            } else if (newX > GameVar.MAP_MAX_X - paddle.getWidth()) {
                newX = GameVar.MAP_MAX_X - paddle.getWidth() - GameVar.PADDLE_OFFSET_X_SCREEN;
            }

            paddle.setPosition(newX, paddle.getY());
        }
    }

    @Override
    public void clear() {
        // TODO: CLEAR
    }

    public void handleCollision(Paddle paddle, GameObject obj) {
        if (obj instanceof PowerUp powerUp) {
            handlePowerUpCollision(paddle, powerUp);
        }
    }

    public void resetPaddle(Paddle paddle) {
        paddle.resetState();
    }

    private void handlePowerUpCollision(Paddle paddle, PowerUp powerUp) {
        // TODO: IMPROVE POWERUP FOR THIS
        
        
        // powerUp.apply(paddle);
    }
}
