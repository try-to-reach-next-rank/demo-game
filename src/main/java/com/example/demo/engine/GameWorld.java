package com.example.demo.engine;

import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.model.core.ThePool;
import com.example.demo.model.core.entities.*;
import com.example.demo.model.core.factory.*;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.model.state.ActivePowerUpData;
import com.example.demo.model.state.BrickData;
import com.example.demo.model.state.GameState;
import com.example.demo.model.state.PowerUpData;
import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.utils.GameStateRestore;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.GameVar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameWorld {
    private static final Logger log = LoggerFactory.getLogger(GameWorld.class);
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks = new Brick[0];
    private int currentLevel = GameVar.START_LEVEL;
    private GameStateRestore gameStateRestore;

    private final List<Wall> walls = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();

    // --- Getters / Setters ---
    public Ball getBall() { return ball; }
    public List<Ball> getBalls() { return List.of(ball); }
    public void setBall(Ball ball) { this.ball = ball; }

    public Paddle getPaddle() { return paddle; }
    public void setPaddle(Paddle paddle) { this.paddle = paddle; }

    public Brick[] getBricks() { return bricks; }
    public void setBricks(Brick[] bricks) { this.bricks = Objects.requireNonNullElse(bricks, new Brick[0]); }

    public List<PowerUp> getPowerUps() { return powerUps; }
    public List<Wall> getWalls() { return walls; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }

    public void applyState(GameState loadedState) {
        gameStateRestore.apply(loadedState, this);
    }

    // === NEW: Get all GameObjects in the world ===
    public List<GameObject> getAllObjects() {
        List<GameObject> all = new ArrayList<>();
        if (paddle != null) all.add(paddle);
        if (ball != null) all.add(ball);
        // TODO: Improve getAllObjects for bricks
        // if (bricks != null) for (Brick b : bricks) all.add(b);
        if (walls != null) all.addAll(walls);
        if (powerUps != null) all.addAll(powerUps);
        return all;
    }
}
