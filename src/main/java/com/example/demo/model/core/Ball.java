package com.example.demo.model.core;

import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Vector2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Ball extends GameObject {
    private final double                speed;   // base speed (px/sec)
    private boolean                     stuck;
    private final                       Paddle paddle;
    private final List<ActiveEffect>    activeEffectList = new ArrayList<>();
    private Vector2D velocity;

    private static class ActiveEffect {
        String type;
        long expireAt;

        ActiveEffect(String type, long duration) {
            this.type = type;
            this.expireAt = System.currentTimeMillis() + duration;
        }
    }

    public Ball(Paddle paddle) {
        super("/images/Ball.png", GameVar.INIT_BALL_X, GameVar.INIT_BALL_Y);
        this.paddle = paddle;
        this.speed = 300;
        initBall();
    }

    private void initBall() {
        velocity = new Vector2D(0, -1);
        stuck = true;
        activeEffectList.clear();
        resetState();
    }

    public void update(double deltaTime) {
        if (stuck) {
            // Ball floats above paddle until launch
            setPosition(
                    paddle.getX() + paddle.getWidth() / 2.0 - getWidth() / 2.0,
                    paddle.getY() - getHeight()
            );
            return;
        }

        updatePowerUps();

        double currentSpeed = isAccelerated()
                ? speed * GameVar.ACCELERATED_SPEED_MULTIPLIER
                : speed;

        Vector2D step = velocity.normalize().multiply(currentSpeed * deltaTime);
        x += step.x;
        y += step.y;

        // Missed paddle
        if (y >= GlobalVar.HEIGHT) {
            stuck = true;
            resetState();
        }

        setPosition(x, y);
    }

    public void resetState() {
        setPosition(
                paddle.getX() + paddle.getWidth() / 2.0 - getWidth() / 2.0,
                paddle.getY() - getHeight()
        );
        stuck = true;
        velocity = new Vector2D(0, -1);
        activeEffectList.clear();
    }

    public void release() {
        if (stuck) {
            stuck = false;
            velocity = new Vector2D(0, -1);
        }
    }

    public void activatePowerUp(PowerUp p) {
        String type = p.getType();
        if (type == null) return;

        switch (type) {
            case "ACCELERATE":
                activeEffectList.add(new ActiveEffect("ACCELERATE", 5000)); // 5s
                break;
            // TODO: add more powerups
        }
    }

    public boolean isAccelerated() {
        long now = System.currentTimeMillis();
        return activeEffectList.stream().anyMatch(e -> e.type.equals("ACCELERATE") && now < e.expireAt);
    }

    public boolean isStuck() {
        return stuck;
    }

    public void updatePowerUps() {
        long now = System.currentTimeMillis();
        Iterator<ActiveEffect> it = activeEffectList.iterator();
        while (it.hasNext()) {
            ActiveEffect e = it.next();
            if (now > e.expireAt) {
                it.remove();
            }
        }
    }

    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D v) { velocity = v.normalize(); }
}
