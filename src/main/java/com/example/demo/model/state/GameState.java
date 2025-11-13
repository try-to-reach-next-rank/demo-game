package com.example.demo.model.state;

import com.example.demo.engine.GameWorld; // Import GameWorld
import com.example.demo.model.core.PowerUp;
import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.model.core.bricks.Brick;
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
        // có thể thêm score và lives ở đây nếu cần
    // private int score;
    // private int lives;

    private PaddleData paddleData;
    private BallData ballData;
    private List<PowerUpData> powerUpsData;
    private List<ActivePowerUpData> activePowerUpsData;
    private List<BrickData> bricksData;

    //private int[][] bricksHealthMatrix; // dạng ma trận máu brick cho dễ nhìn

    // Đọc thấy ngta bảo vẫn nên có constructor rỗng ig
    public GameState() {
        powerUpsData = new ArrayList<>();
        bricksData = new ArrayList<>();
        activePowerUpsData = new ArrayList<>();
    }

    // === CONSTRUCTOR MỚI: Tự động thu thập toàn bộ trạng thái game ===
    public GameState(GameWorld world) {
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
        this.powerUpsData = new ArrayList<>();
        for (PowerUp p : world.getPowerUps()) {
            if (p.isVisible()) {
                this.powerUpsData.add(new PowerUpData(p));
            }
        }

        // Active Power-ups
        this.activePowerUpsData = new ArrayList<>();
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
        this.bricksData = new ArrayList<>();
        Brick[] bricks = world.getBricks();
        for (int i = 0; i < bricks.length; i++) {
            this.bricksData.add(new BrickData(i, bricks[i]));
        }
    }

    public long getRealHighScore() {
        return realHighScore;
    }

    public void setRealHighScore(long realHighScore) {
        this.realHighScore = realHighScore;
    }

    public double getPlayElapsedSeconds() {
        return playElapsedSeconds;
    }

    public void setPlayElapsedSeconds(double playElapsedSeconds) {
        this.playElapsedSeconds = playElapsedSeconds;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public List<ActivePowerUpData> getActivePowerUpsData() {
        return activePowerUpsData;
    }

    public void setActivePowerUpsData(List<ActivePowerUpData> activePowerUpsData) {
        this.activePowerUpsData = activePowerUpsData;
    }

    public double getCurrentTrackTime() {
        return currentTrackTime;
    }

    public void setCurrentTrackTime(double currentTrackTime) {
        this.currentTrackTime = currentTrackTime;
    }

    public String getCurrentTrackName() {
        return currentTrackName;
    }

    public void setCurrentTrackName(String currentTrackName) {
        this.currentTrackName = currentTrackName;
    }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public PaddleData getPaddleData() { return paddleData; }
    public void setPaddleData(PaddleData paddleData) { this.paddleData = paddleData; }

    public BallData getBallData() { return ballData; }
    public void setBallData(BallData ballData) { this.ballData = ballData; }

    public List<PowerUpData> getPowerUpsData() { return powerUpsData; }
    public void setPowerUpsData(List<PowerUpData> powerUpsData) { this.powerUpsData = powerUpsData; }

    public List<BrickData> getBricksData() { return bricksData; }
    public void setBricksData(List<BrickData> bricksData) { this.bricksData = bricksData; }
}