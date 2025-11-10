package com.example.demo.engine;

import com.example.demo.model.core.*;
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
    private PowerUpSystem powerUpSystem; // DUPLICATE REMOVED
    private Brick[] bricks = new Brick[0];
    private int currentLevel = GameVar.START_LEVEL;
    private GameStateRestore gameStateRestore; // DUPLICATE REMOVED

    private final List<Wall> walls = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Updatable> updatables = new ArrayList<>();
    private int currentScore = 0;
    private int highScore = 0;
    private int lastAddedScore = 0;

    // TIMER PLAY
    private double playElapsedSeconds = 0.0;
    private boolean playTimerRunning = false;

    public void startPlayTimer() {
        playElapsedSeconds = 0.0;
        playTimerRunning = true;
    }

    public void pausePlayTimer() {
        playTimerRunning = false;
    }

    public void resumePlayTimer() {
        playTimerRunning = true;
    }

    private void updatePlayTime(double deltaTime) {
        if (playTimerRunning) {
            playElapsedSeconds += deltaTime;
        }
    }

    public long computeRealHighScore() {
        double remaining = 100.0 - playElapsedSeconds;
        if (remaining < 0) remaining = 0;
        if (highScore <= 0) return 0;
        double value = remaining * highScore;
        return (long)Math.round(value);
    }

    // --- Getters / Setters ---
    // NOTE: Các cặp getter/setter bị duplicate đã được hợp nhất hoặc xóa

    public PowerUpSystem getPowerUpSystem() {
        return powerUpSystem;
    }
    public void setPowerUpSystem(PowerUpSystem powerUpSystem) {
        this.powerUpSystem = powerUpSystem;
    }

    public double getPlayElapsedSeconds() {
        return playElapsedSeconds;
    }

    public void setPlayElapsedSeconds(double playElapsedSeconds) {
        this.playElapsedSeconds = playElapsedSeconds;
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

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getLastAddedScore() {
        return lastAddedScore;
    }

    public boolean isLevelComplete() {
        return getRemainingBricksCount() == 0;
    }

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
        updatePlayTime(deltaTime);
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
        gameStateRestore.apply(loadedState, this);
    }
    
    // Phương thức `apply` trong `GameStateRestore` (đã loại bỏ từ khối mã gốc) 
    // sẽ thực hiện phần còn lại của logic khôi phục trạng thái.

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

    // ADDING SCORE INTO CURRENTSCORE
    public void addScore(int points, double x, double y) {
        this.currentScore += points;
        this.lastAddedScore = points;
        if (this.currentScore > this.highScore) {
            this.highScore = this.currentScore; // cập nhật highScore
        }
        log.info("[SCORES] currentScore={} | highScore={}", currentScore, highScore);
    }

    // ========== NEW: Brick Counting Methods ==========

    public int getRemainingBricksCount() {
        if (bricks == null || bricks.length == 0) {
            return 0;
        }

        int count = 0;
        for (Brick brick : bricks) {
            // Only count bricks that are:
            // 1. Not destroyed
            if (!brick.isDestroyed()) {
                count++;
            }
        }
        return count;
    }

    public void verifyBrickScores() {
        int errors = 0;
        for (int i = 0; i < bricks.length; i++) {
            Brick b = bricks[i];
            int expected;
            if (b.getInitialHealth() == Integer.MAX_VALUE) {
                expected = 0;
            } else if (b.getInitialHealth() <= 0) {
                expected = 0;
            } else {
                expected = b.getInitialHealth() * 10;
            }
            int actual = b.getScoreValue();
            if (actual != expected) {
                log.warn("[BRICK SCORE MISMATCH] index={} initialHealth={} expectedScore={} actualScore={}",
                        i, b.getInitialHealth(), expected, actual);
                errors++;
            } else {
                // Có thể bật khi debug chi tiết:
                // log.info("[BRICK SCORE OK] index={} health={} score={}", i, b.getInitialHealth(), actual);
            }
        }
        log.info("[VERIFY BRICKS] totalBricks={} mismatches={}", bricks.length, errors);
    }
}