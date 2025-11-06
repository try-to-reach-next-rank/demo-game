package com.example.demo.model.core;

import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.BallData;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class Ball extends ImageObject<BallData> {

    // ===================== Fields =====================
    private final double baseSpeed = GameVar.BASE_SPEED_BALL;
    private final Paddle paddle;

    private boolean stuck;
    private boolean accelerated;
    private boolean stronger;
    private boolean stopTime;

    private double elapsedTime = 0;
    private Vector2D velocity;
    private Brick lastBrick;

    // ===================== Constructor =====================
    public Ball(Paddle paddle) {
        super("ball", GameVar.INIT_BALL_X, GameVar.INIT_BALL_Y);
        this.paddle = paddle;
        resetState();
    }

    // ===================== State Toggle =====================
    public void toggleStopTime()     { this.stopTime = !this.stopTime; }
    public void toggleAccelerated()  { this.accelerated = !this.accelerated; }
    public void toggleStronger()     { this.stronger = !this.stronger; }

    // ===================== State Control =====================
    public void resetState() {
        stuck = true;
        accelerated = false;
        stronger = false;
        stopTime = false;
        velocity = new Vector2D(GameVar.BALL_INIT_DIR_X, GameVar.BALL_INIT_DIR_Y);
    }

    public void release() {
        if (stuck) {
            stuck = false;
            setVelocity(GameVar.BALL_INIT_DIR_X, GameVar.BALL_INIT_DIR_Y);
        }
    }

    @Override
    public void applyState(BallData data) {
        if (data == null) return;
        this.setPosition(data.getX(), data.getY());
        this.setVelocity(data.getVelocityX(), data.getVelocityY());
        this.setStuck(data.isStuck());
    }

    @Override
    public boolean isStatic() {
        return false;
    }


    // ===================== Getters / Setters =====================
    public boolean isStuck()                     { return stuck; }
    public void setStuck(boolean stuck)          { this.stuck = stuck; }

    public Vector2D getVelocity()                { return velocity; }
    public void setVelocity(Vector2D v)          { this.velocity = v.normalize(); }
    public void setVelocity(double x, double y)  { this.velocity.x = x; this.velocity.y = y; }

    public double getBaseSpeed()                 { return baseSpeed; }

    public boolean isAccelerated()               { return accelerated; }
    public void setAccelerated(boolean b)        { this.accelerated = b; }

    public boolean isStronger()                  { return stronger; }
    public void setStronger(boolean b)           { this.stronger = b; }

    public boolean isStopTime()                  { return stopTime; }
    public void setStopTime(boolean b)           { this.stopTime = b; }

    public double getElapsedTime()               { return elapsedTime; }
    public void setElapsedTime(double t)         { this.elapsedTime = t; }

    public Brick getLastBrick()                  { return lastBrick; }
    public void setLastBrick(Brick brick)        { this.lastBrick = brick; }

}
