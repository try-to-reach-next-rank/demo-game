package com.example.demo.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// adding sfx into ball.java
import com.example.demo.managers.SoundManager;

public class Ball extends GameObject {
    private int                         xdir, ydir;
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
        initBall();
    }

    private void initBall() {
        xdir = 1;
        ydir = -1;
        stuck = true;
        activeEffectList.clear();
        resetState();
    }

    public void move() {
        if(stuck) { // make the ball float on top of paddle until launch
            setPosition(paddle.getX() + paddle.getWidth() / 2.0 - getWidth() / 2.0,
                        paddle.getY() - getHeight());
            return;
        }

        updatePowerUps();

        float currentSpeed = isAccelerated()
                ? VARIABLES.SPEED * VARIABLES.ACCELERATED_SPEED_MULTIPLIER
                : VARIABLES.SPEED;

        x += xdir * currentSpeed;
        y += ydir * currentSpeed;

        System.out.println(currentSpeed);

        if (x <= 0) {
            setXDir(1);
            SoundManager.getInstance().playSound("wall_hit");
        }
        if (x >= VARIABLES.WIDTH - getWidth()) {
            setXDir(-1);
            SoundManager.getInstance().playSound("wall_hit");
        }
        if (y <= 0) {
            setYDir(1);
            SoundManager.getInstance().playSound("wall_hit");
        }

        if(y >= VARIABLES.HEIGHT) {
            stuck = true;
            resetState();
        }

        setPosition(x, y);
    }

    public void resetState() {
        setPosition(paddle.getX() + paddle.getWidth() / 2 - getWidth() / 2,
                    paddle.getY() - getHeight());
        stuck = true;
        ydir = -1;
        activeEffectList.clear();
    }

    public void release() {
        stuck = false;
        xdir = 1;
        ydir = -1;
    }

    public void activatePowerUp(PowerUp p) {
        String type = p.getType();
        if (type == null) {
            return;
        }
        switch (type) {
            case "ACCELERATE":
                activeEffectList.add(new ActiveEffect("ACCELERATE", 5000)); // 5s
                break;
            // More types can be added later:
            // case "BIG_PADDLE": ...
            // case "MULTIBALL": ...
        }
    }

    public boolean isAccelerated() {
        // check if any ACCELERATE effect is still active
        return activeEffectList.stream().anyMatch(e -> e.type.equals("ACCELERATE"));
    }

    public void setXDir(int x) { xdir = x; }
    public void setYDir(int y) { ydir = y; }
    public int getYDir() { return ydir;}

    // check if ball is stuck to the paddle
    public boolean isStuck() {
        return stuck;
    }

    public void updatePowerUps() {
        long now = System.currentTimeMillis();
        Iterator<ActiveEffect> it = activeEffectList.iterator();
        while (it.hasNext()) {
            ActiveEffect e = it.next();
            if (now > e.expireAt) {
                it.remove(); // remove expired buff
            }
        }
    }
}