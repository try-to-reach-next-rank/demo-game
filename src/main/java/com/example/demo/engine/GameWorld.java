package com.example.demo.engine;

import com.example.demo.model.core.*;
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
    private PowerUpSystem powerUpSystem;
    private Brick[] bricks = new Brick[0];
    private int currentLevel = GameVar.START_LEVEL;
    private GameStateRestore gameStateRestore;

    private final List<Wall> walls = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
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
        gameStateRestore.apply(loadedState, this);
    }
}
