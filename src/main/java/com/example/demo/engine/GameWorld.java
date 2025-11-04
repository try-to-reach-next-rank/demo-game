package com.example.demo.engine;

import com.example.demo.model.core.*;
import com.example.demo.model.state.ActivePowerUpData;
import com.example.demo.model.state.BrickData;
import com.example.demo.model.state.GameState;
import com.example.demo.model.state.PowerUpData;
import com.example.demo.model.system.PowerUpSystem;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.GameVar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameWorld {
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks = new Brick[0];
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Wall> walls = new ArrayList<>();
    private int currentLevel = GameVar.START_LEVEL;
    private PowerUpSystem powerUpSystem;
    private final List<Updatable> updatables = new ArrayList<>();

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
    public List<Updatable> getUpdatables() { return updatables; }

    // convenience reset helper
    public void resetForNewLevel() {
        powerUps.clear();
        if (ball != null) ball.resetState();
        if (paddle != null) paddle.resetState();
    }

    public void init() {
        paddle = new Paddle();
        ball = new Ball(paddle);

        powerUps.clear();
        walls.clear();

        bricks = new Brick[0];
    }

    public void update(double deltaTime) {
        for (Updatable u : updatables) {
            u.update(deltaTime);
        }
    }

    // --- Register an updatable system ---
    public void registerUpdatable(Updatable system) {
        if (!updatables.contains(system)) {
            updatables.add(system);
        }
    }

    // --- Clear all updatables ---
    public void clearUpdatables() {
        updatables.clear();
    }

    public void applyState(GameState loadedState) {
        // SECTION 1: Setup Level
        setCurrentLevel(loadedState.getCurrentLevel());
        Sound.getInstance().playMusic(loadedState.getCurrentTrackName(), loadedState.getCurrentTrackTime());

        // SECTION 2: Apply Entity States
        Ball ball = getBall();
        Paddle paddle = getPaddle();
        Brick[] bricks = getBricks();
        paddle.applyState(loadedState.getPaddleData());
        ball.applyState(loadedState.getBallData());

        for (BrickData data : loadedState.getBricksData()) {
            if (data.getId() >= 0 && data.getId() < bricks.length) {
                bricks[data.getId()].applyState(data);
            }
        }

        // SECTION 3: Apply Relationships
        if (ball.isStuck()) {
            ball.alignWithPaddle(GameVar.BALL_OFFSET_Y, GameVar.BALL_ALIGN_LERP_FACTOR);
        }

        // Falling Power-Ups
        getPowerUps().clear();
        for (PowerUpData powerUpData : loadedState.getPowerUpsData()) {
            PowerUp p = ThePool.PowerUpPool.acquire(powerUpData.getType());
            p.setPosition(powerUpData.getX(), powerUpData.getY());
            p.setVisible(powerUpData.isVisible());
            getPowerUps().add(p);
        }

        // Active Power-ups
        PowerUpSystem currentPowerUpSystem = getPowerUpSystem();
        if (currentPowerUpSystem != null) {
            currentPowerUpSystem.reset();

            if (loadedState.getActivePowerUpsData() != null) {
                for (ActivePowerUpData activeData : loadedState.getActivePowerUpsData()) {
                    currentPowerUpSystem.activateFromSave(activeData);
                }
            }
        }
    }
}
