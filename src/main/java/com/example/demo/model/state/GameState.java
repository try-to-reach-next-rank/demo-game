package com.example.demo.model.state;

import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.bricks.SteelBrick;
import com.example.demo.utils.Sound;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int currentLevel;
    private String currentTrackName;
    private double currentTrackTime;
    private int highScore;
    private int currentScore;
    private double playElapsedSeconds;
    private long realHighScore;

    private PaddleData paddleData;
    private BallData ballData;
    private List<PowerUpData> powerUpsData;
    private List<ActivePowerUpData> activePowerUpsData;
    private List<BrickData> bricksData;

    public GameState(GameWorld world) {
        // Khởi tạo các list
        this.bricksData = new ArrayList<>();
        this.powerUpsData = new ArrayList<>();
        this.activePowerUpsData = new ArrayList<>();

        // Level, Paddle và Ball
        this.playElapsedSeconds = world.getPlayElapsedSeconds();
        this.realHighScore = world.computeRealHighScore();
        this.currentScore = world.getCurrentScore();
        this.highScore = world.getHighScore();
        this.currentLevel = world.getCurrentLevel();
        this.paddleData = new PaddleData(world.getPaddle());
        this.ballData = new BallData(world.getBall());


        // Laays nhạc và thwofi gian
        Sound soundManager = Sound.getInstance();
        this.currentTrackName = soundManager.getCurrentTrackName();
        this.currentTrackTime = soundManager.getCurrentMusicTime();

        // Power-ups
        for (PowerUp p : world.getPowerUps()) {
            if (p.isVisible()) {
                this.powerUpsData.add(new PowerUpData(p));
            }
        }

        // Active Power-ups
        PowerUpSystem powerUpSystem = world.getPowerUpSystem();
        if (powerUpSystem != null) {
            for (PowerUp activePowerUp : powerUpSystem.getActivePowerUps()) {
                if (activePowerUp.isActive()) {
                    ActivePowerUpData activeData = new ActivePowerUpData();
                    activeData.setType(activePowerUp.getType());
                    activeData.setRemainingDuration(activePowerUp.getRemainingDuration());
                    this.activePowerUpsData.add(activeData);
                }
            }
        }

        /**
         * Đây là hàm cũ cái này sẽ tạo dâta dạng 1 chiều khá khó nhìn
         */
        Brick[] bricks = world.getBricks();
        for (int i = 0; i < bricks.length; i++) {
            Brick brick = bricks[i];
            String type = brick instanceof SteelBrick ? "STEEL" : "NORMAL";
            this.bricksData.add(new BrickData(i, brick, type));
        }
    }

    public int getHighScore() {
        return highScore;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public double getCurrentTrackTime() {
        return currentTrackTime;
    }

    public String getCurrentTrackName() {
        return currentTrackName;
    }

    public int getCurrentLevel() { return currentLevel; }
    public PaddleData getPaddleData() { return paddleData; }

    public BallData getBallData() { return ballData; }

    public List<PowerUpData> getPowerUpsData() { return powerUpsData; }

    public List<BrickData> getBricksData() { return bricksData; }
}