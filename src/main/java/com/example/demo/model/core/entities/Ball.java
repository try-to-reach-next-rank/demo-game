package com.example.demo.model.core.entities;

import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.gameobjects.ImageObject;
import com.example.demo.model.state.BallData;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class Ball extends ImageObject<BallData> {
    private final double BASE_SPEED = GameVar.BASE_SPEED_BALL;
    private boolean beingHeld;
    private boolean stuck;
    private boolean accelerated;
    private boolean stronger;
    private boolean stopTime;
    private double elapsedTime = 0;
    private Vector2D velocity;

    private Paddle stuckPaddle;
    private Brick lastBrick;

    public Ball() {
        super(GameVar.INIT_BALL_X, GameVar.INIT_BALL_Y);
        setImageKey("ball");
        // TODO: FOR TEST PORTAL
        setSize(32, 32);
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
        this.stuck = true;
        this.accelerated = false;
        this.stronger = false;
        this.stopTime = false;
        this.elapsedTime = 0.0;
        setVelocity(GameVar.BALL_INIT_DIR_X, GameVar.BALL_INIT_DIR_Y);
    }

    public void release() {
        if (stuck) {
            stuck = false;
            setVelocity(GameVar.BALL_INIT_DIR_X, GameVar.BALL_INIT_DIR_Y);
        }
    }

    // --- Data ---
    @Override
    public void applyState(BallData data) {
        super.applyState(data);
        this.setVelocity(data.getVelocityX(), data.getVelocityY());
        this.setStuck(data.isStuck());
    }

    // --- Getters / setters ---
    public boolean isHeldByEffect() { return beingHeld; }
    public void setHeldByEffect(boolean held) { this.beingHeld = held; }

    public boolean isStuck() { return this.stuck; }
    public void setStuck(boolean stuck) { this.stuck = stuck; }

    public boolean isAccelerated() { return this.accelerated; }
    public void setAccelerated(boolean accelerated) { this.accelerated = accelerated; }

    public boolean isStronger() { return this.stronger; }
    public void setStronger(boolean stronger){ this.stronger = stronger; }

    public boolean isStopTime() { return stopTime; }
    public void setStopTime(boolean stopTime) { this.stopTime = stopTime; }

    public double getElapsedTime(){ return this.elapsedTime; }
    public void setElapsedTime(double x){ this.elapsedTime = x; }

    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D v) { this.velocity = v.normalize(); }
    public void setVelocity(double x, double y){ setVelocity(new Vector2D(x, y)); }
    
    public double getBaseSpeed() { return this.BASE_SPEED; }

    // --- Getters / Setters with other object ---
    public Brick getLastBrick() { return this.lastBrick; }
    public void setLastBrick(Brick brick) { this.lastBrick = brick; }

    public Paddle getStuckPaddle() { return this.stuckPaddle; }
    public void setStuckPaddle(Paddle paddle) { this.stuckPaddle = paddle; }
}
