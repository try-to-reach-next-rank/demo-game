package com.example.demo.model.state;

import com.example.demo.model.core.Ball;

public class BallData {
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private boolean isStuck;

    public BallData() {}

    public BallData(Ball ball) {
        this.x = ball.getX();
        this.y = ball.getY();
        if (ball.getVelocity() != null) {
            this.velocityX = ball.getVelocity().x;
            this.velocityY = ball.getVelocity().y;
        }
        this.isStuck = ball.isStuck();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public boolean isStuck() { return isStuck; }
}