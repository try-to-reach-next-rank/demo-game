package com.example.demo.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// adding sfx into ball.java
import com.example.demo.managers.SoundManager;

public class Ball extends GameObject {
    private double                      dx, dy;  // continuous velocity
    private final double                speed;   // base speed (px/sec)
    private boolean                     stuck;
    private final                       Paddle paddle;
    private final List<ActiveEffect>    activeEffectList = new ArrayList<>();

    private static class ActiveEffect {
        String type;
        long expireAt;

        ActiveEffect(String type, long duration) {
            this.type = type;
            this.expireAt = System.currentTimeMillis() + duration;
        }
    }

    public Ball(Paddle paddle) {
        super("/images/Ball.png", VARIABLES.INIT_BALL_X, VARIABLES.INIT_BALL_Y);
        this.paddle = paddle;
        this.speed = 300;
        initBall();
    }

    private void initBall() {
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
                ? speed * VARIABLES.ACCELERATED_SPEED_MULTIPLIER
                : speed;

        double vx = dx * currentSpeed;
        double vy = dy * currentSpeed;

        // Move with deltaTime (seconds)
        x += vx * deltaTime;
        y += vy * deltaTime;

        System.out.println("Ball released: dx=" + dx + " dy=" + dy);

        // Missed paddle
        if (y >= VARIABLES.HEIGHT) {
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
        dx = 0;
        dy = -1; // upwards
        activeEffectList.clear();
    }

    public void release() {
        if (stuck) {
            stuck = false;
            dx = 0;
            dy = -1.0;
        }
    }

    private void normalize() {
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len != 0) {
            dx /= len;
            dy /= len;
        }
    }

    public void setVelocity(double angle) {
        dx = Math.cos(angle);
        dy = -Math.sin(angle); // negative Y = up
        normalize();
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

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDx(double v) {
        dx = v;
    }

    public void setDy(double v) {
        dy = v;
    }
}
