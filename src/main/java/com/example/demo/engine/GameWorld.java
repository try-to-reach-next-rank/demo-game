package com.example.demo.engine;

import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.bricks.SteelBrick;
import com.example.demo.model.core.factory.MovedWallFactory;
import com.example.demo.model.core.factory.PortalFactory;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.entities.Wall;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.model.state.GameState;
import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.utils.GameStateRestore;
import com.example.demo.utils.var.GameVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameWorld {
    private static GameWorld instance = null;
    private static final Logger log = LoggerFactory.getLogger(GameWorld.class);
    private Ball ball;
    private Paddle paddle;
    private PowerUpSystem powerUpSystem;
    private PortalFactory portalFactory;
    private MovedWallFactory movedwallFactory;
    private Brick[] bricks = new Brick[0];
    private int currentLevel = GameVar.START_LEVEL;
    private GameStateRestore gameStateRestore;

    private final List<Wall> walls = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private int currentScore = 0;
    private int highScore = 0;
    private int lastAddedScore = 0;

    // explode all bricks
    private boolean exploding = false;
    private int explodeCounter = 0;
    private int explodeTickCounter = 0;

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

    public long computeRealHighScore() {
        double remaining = 100.0 - playElapsedSeconds;
        if (remaining < 0) remaining = 0;
        if (highScore <= 0) return 0;
        double value = remaining * highScore;
        return Math.round(value);
    }

    // --- Getters / Setters ---
    // NOTE: Các cặp getter/setter bị duplicate đã được hợp nhất hoặc xóa

    public PowerUpSystem getPowerUpSystem() {
        return powerUpSystem;
    }

    public double getPlayElapsedSeconds() {
        return playElapsedSeconds;
    }

     public void setGameStateRestore(GameStateRestore gameStateRestore) {
         this.gameStateRestore = gameStateRestore;
     }

    public Ball getBall() { return ball; }
    public List<Ball> getBalls() { return List.of(ball); }
    public void setBall(Ball ball) { this.ball = ball; }

    public Paddle getPaddle() { return paddle; }
    public List<Paddle> getPaddles() { return List.of(paddle); }
    public void setPaddle(Paddle paddle) { this.paddle = paddle; }

    public List<Brick> getBrickss() { return Arrays.asList(bricks); }
    public Brick[] getBricks() { return bricks; }
    public void setBricks(Brick[] bricks) { this.bricks = Objects.requireNonNullElse(bricks, new Brick[0]); }

    public List<PowerUp> getPowerUps() { return powerUps; }
    public List<Wall> getWalls() { return walls; }

    public PortalFactory getPortalFactory() { return this.portalFactory; }
    public void setPortalFactory(PortalFactory pf) { this.portalFactory = pf; } 

    public MovedWallFactory getMovedWallFactory() { return this.movedwallFactory; }
    public void setMovedWallFactory(MovedWallFactory f) { this.movedwallFactory = f; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }
    
    public int getCurrentScore() { return currentScore; }
    public void setCurrentScore(int currentScore) { this.currentScore = currentScore; }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    // convenience reset helper
    public void resetForNewLevel() {
        powerUps.clear();
        if (ball != null) ball.resetState();
        if (paddle != null) paddle.resetState();
    }

    public void applyState(GameState loadedState) {
         gameStateRestore.apply(loadedState, this);
    }

    public List<GameObject> getAllObjects() {
        List<GameObject> all = new ArrayList<>();
        if (paddle != null) all.add(paddle);
        if (ball != null) all.add(ball);
        // TODO: Improve getAllObjects for bricks
        // if (bricks != null) for (Brick b : bricks) all.add(b);
        if (walls != null) all.addAll(walls);
        if (powerUps != null) all.addAll(powerUps);
        if (portalFactory != null) all.addAll(portalFactory.getPortals());
        if (movedwallFactory != null) all.addAll(movedwallFactory.getMovedWalls());

        return all;
    }

    // TODO: FOR TESTING, DELETE LATER
    public List<GameObject> getObjects() {
        List<GameObject> all = new ArrayList<>();

        if (paddle != null) all.add(paddle);
        if (ball != null) all.add(ball);

        if (bricks != null)
            for (Brick b : bricks)
                if (b != null) all.add(b);

        if (walls != null && !walls.isEmpty())
            all.addAll(walls);

        if (powerUps != null && !powerUps.isEmpty())
            all.addAll(powerUps);

        if (portalFactory != null) 
            all.addAll(portalFactory.getPortals());

        if (movedwallFactory != null)
            all.addAll(movedwallFactory.getMovedWalls());

        return all;
    }

    // ADDING SCORE INTO CURRENTSCORE
    public void addScore(int points, double x, double y) {
        this.currentScore += points;
        this.lastAddedScore = points;
        if (this.currentScore > this.highScore) {
            this.highScore = this.currentScore; // cập nhật highScore
        }
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
            // 2. Not steel brick
            if (brick instanceof SteelBrick) continue;
            if (!brick.isDestroyed()) {
                count++;
            }
        }
        return count;
    }

    public void startExplodeAllBricks() {
        exploding = true;
        explodeCounter = 0;
        explodeTickCounter = 0;
        log.info("Start exploding all bricks!");
    }

    public void updateExplode() {
        explodeTickCounter++;

        if (exploding) {
            int explodeInterval = 3;       // Mỗi 3 tick (giống reveal = 5)
            int bricksPerTick = 5;         // Nổ 5 gạch mỗi lần (giống reveal = 3)

            if (explodeTickCounter >= explodeInterval) {
                explodeTickCounter = 0;

                if (bricks != null) {
                    for (int i = 0; i < bricksPerTick && explodeCounter < bricks.length; i++) {
                        Brick brick = bricks[explodeCounter];

                        if (brick != null && !brick.isDestroyed()) {
                            // Cộng điểm
                            double centerX = brick.getX() + brick.getWidth() / 2;
                            double centerY = brick.getY() + brick.getHeight() / 2;
                            addScore(brick.getScoreValue(), centerX, centerY);

                            // Phá gạch
                            brick.takeDamage(brick.getHealth());
                        }

                        explodeCounter++;
                    }

                    // Khi nổ hết
                    if (explodeCounter >= bricks.length) {
                        exploding = false;
                        explodeCounter = 0;
                        explodeTickCounter = 0;
                        log.info("All bricks exploded!");
                    }
                }
            }
        }
    }


    public static GameWorld getInstance() {
        if (instance == null) {
            instance = new GameWorld();
            return instance;
        }
        return instance;
    }
}
