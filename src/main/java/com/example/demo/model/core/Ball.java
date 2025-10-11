package com.example.demo.model.core;

import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.Vector2D;

public class Ball extends GameObject {
    private final double baseSpeed = 300.0;
    private boolean stuck;
    private boolean accelerated;
    private Vector2D velocity;
    private final Paddle paddle;

    public Ball(Paddle paddle) {
        super("/images/Ball.png", GameVar.INIT_BALL_X, GameVar.INIT_BALL_Y);
        this.paddle = paddle;
        resetState();
    }

    public void resetState() {
        setPosition(
                paddle.getX() + paddle.getWidth() / 2.0 - getWidth() / 2.0,
                paddle.getY() - getHeight()
        );
        stuck = true;
        accelerated = false;
        velocity = new Vector2D(0, -1);
    }

    public void release() {
        if (stuck) {
            stuck = false;
            velocity = new Vector2D(0, -1);
        }
    }

    // Getters and setters
    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D v) { this.velocity = v.normalize(); }

    public boolean isStuck() { return stuck; }
    public boolean isAccelerated() { return accelerated; }
    public void setAccelerated(boolean accelerated) { this.accelerated = accelerated; }

    public double getBaseSpeed() { return baseSpeed; }
}
