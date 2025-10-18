package com.example.demo.model.core;

import com.example.demo.model.state.BallData;
import com.example.demo.controller.AssetManager;
import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.Vector2D;

public class Ball extends GameObject {
    private final double baseSpeed = 300.0;
    private boolean stuck;
    private Vector2D velocity;
    private final Paddle paddle;

    private boolean accelerated;
    private boolean stronger;
    private boolean stopTime;

    public Ball(Paddle paddle) {
        super(AssetManager.getInstance().getImage("ball"), GameVar.INIT_BALL_X, GameVar.INIT_BALL_Y);
        this.paddle = paddle;
        resetState();
    }

    public void resetState() {
        alignWithPaddle(10, 1.0);
        stuck = true;
        accelerated = false;
        stronger = false;
        velocity = new Vector2D(0, -1);
    }

    public void release() {
        if (stuck) {
            stuck = false;
            setVelocity(0, -1);
        }
    }

    // Thêm vào lớp Ball.java
    public void applyState(BallData data) {
        if (data == null) return;
        this.setPosition(data.getX(), data.getY());
        this.setVelocity(data.getVelocityX(), data.getVelocityY());
        this.setStuck(data.isStuck());
    }

    // Getters and setters
    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }

    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D v) {
        this.velocity = v.normalize(); }

    public void setVelocity(double x, double y){
        this.velocity.x = x;
        this.velocity.y = y;
    }
    public boolean isStuck() { return stuck; }
    public double getBaseSpeed() { return baseSpeed; }

    public boolean isAccelerated() { return accelerated; }
    public void setAccelerated(boolean accelerated) { this.accelerated = accelerated; }


    public boolean isStronger() { return stronger; }
    public void setStronger(boolean stronger){ this.stronger = stronger; }

    public boolean isStopTime() { return stopTime; }
    public void setStopTime(boolean stopTime) { this.stopTime = stopTime; }

    public void alignWithPaddle(double offsetY, double lerpFactor) {
        double targetX = paddle.getX() + paddle.getWidth() / 2.0 - getWidth() / 2.0;
        double targetY = paddle.getY() - getHeight() - offsetY;

        if (lerpFactor >= 1.0) {
            x = targetX;
            y = targetY;
        } else {
            x += (targetX - x) * lerpFactor;
            y += (targetY - y) * lerpFactor;
        }

        double minX = paddle.getX();
        double maxX = paddle.getX() + paddle.getWidth() - getWidth();
        if (x < minX) x = minX;
        if (x > maxX) x = maxX;
        setPosition(x, y);
    }

}
