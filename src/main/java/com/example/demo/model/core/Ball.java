package com.example.demo.model.core;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.BallData;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class Ball extends ImageObject {
    private final double baseSpeed = GameVar.BASE_SPEED_BALL;
    private boolean stuck;
    private Vector2D velocity;
    private final Paddle paddle;

    private boolean accelerated;
    private boolean stronger;
    private boolean stopTime;
    private double elapsedTime = 0;

    private Brick lastBrick;

    public Ball(Paddle paddle) {
        super("ball", GameVar.INIT_BALL_X, GameVar.INIT_BALL_Y);
        this.paddle = paddle;
        resetState();
    }

    public void toggleStopTime() {
        this.stopTime = !this.stopTime;
    }

    public void toggleAccelerated() {
        this.accelerated = !this.accelerated;
    }

    public void toggleStronger() {
        this.stronger = !this.stronger;
    }


    public void resetState() {
        alignWithPaddle(GameVar.BALL_OFFSET_Y, GameVar.BALL_ALIGN_LERP_FACTOR);
        stuck = true;
        accelerated = false;
        stronger = false;
        velocity = new Vector2D(GameVar.BALL_INIT_DIR_X, GameVar.BALL_INIT_DIR_Y);
    }

    public void release() {
        if (stuck) {
            stuck = false;
            setVelocity(GameVar.BALL_INIT_DIR_X, GameVar.BALL_INIT_DIR_Y);
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

    public Vector2D generateRandomPunchVelocity() {
        double angle = Math.toRadians(45 + Math.random() * 90); // between 45°–135°
        double speed = 350 + Math.random() * 150;
        return new Vector2D(Math.cos(angle) * speed, -Math.abs(Math.sin(angle)) * speed);
    }

    public double getElapsedTime(){ return elapsedTime; }
    public void setElapsedTime(double x){ elapsedTime = x; }

    @Override
    public boolean isStatic() {
        return false;
    }

    public Brick getLastBrick(){
        return lastBrick;
    }
    public void setLastBrick(Brick brick){
        lastBrick = brick;
    }

}
