package com.example.demo.model.state;

import com.example.demo.model.core.Ball;
import com.example.demo.model.state.gameobjectdata.GameObjectData;
import com.example.demo.model.state.gameobjectdata.ImageObjectData;

public class BallData extends ImageObjectData {
    private double velocityX;
    private double velocityY;
    private boolean isStuck;

    public BallData(Ball ball) {
        super(ball);
        if (ball.getVelocity() != null) {
            this.velocityX = ball.getVelocity().x;
            this.velocityY = ball.getVelocity().y;
        }
        this.isStuck = ball.isStuck();
    }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public boolean isStuck() { return isStuck; }
}