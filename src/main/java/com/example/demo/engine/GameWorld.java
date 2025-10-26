package com.example.demo.engine;

import com.example.demo.controller.EffectManager;
import com.example.demo.model.core.*;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.model.system.PowerUpSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Small aggregate that contains references to the current world objects.
 * Keeps managers decoupled from GameManager's internals.
 */
public class GameWorld {
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks = new Brick[0];
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Wall> walls = new ArrayList<>();
    private int currentLevel = 2;
    private PowerUpSystem powerUpSystem;

    public PowerUpSystem getPowerUpSystem() {
        return powerUpSystem;
    }

    public void setPowerUpSystem(PowerUpSystem powerUpSystem) {
        this.powerUpSystem = powerUpSystem;
    }

    public Ball getBall() { return ball; }
    public void setBall(Ball ball) { this.ball = ball; }

    public Paddle getPaddle() { return paddle; }
    public void setPaddle(Paddle paddle) { this.paddle = paddle; }

    public Brick[] getBricks() { return bricks; }
    public void setBricks(Brick[] bricks) { this.bricks = Objects.requireNonNullElse(bricks, new Brick[0]); }

    public List<PowerUp> getPowerUps() { return powerUps; }

    public List<Wall> getWalls() { return walls; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }

    // convenience reset helper
    public void resetForNewLevel() {
        powerUps.clear();
        if (ball != null) ball.resetState();
        if (paddle != null) paddle.resetState();
        EffectManager.getInstance().clear();
    }

    public List<GameObject> getAllObjects() {
        List<GameObject> all = new ArrayList<>();
        if (paddle != null) all.add(paddle);
        if (ball != null) all.add(ball);
        if (bricks != null) for (Brick b : bricks) all.add(b);
        if (walls != null) all.addAll(walls);
        if (powerUps != null) all.addAll(powerUps);
        return all;    
    }
}
