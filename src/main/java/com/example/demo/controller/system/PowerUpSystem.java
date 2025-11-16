package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.Paddle;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.entities.ThePool;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.var.GameVar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpSystem implements Updatable {
    private final List<PowerUp> activePowerUps = new ArrayList<>();
    private final List<PowerUp> worldPowerUps;
    private final Ball ball;
    private final Paddle paddle;

    public PowerUpSystem(Ball ball, Paddle paddle, List<PowerUp> worldPowerUps) {
        this.ball = ball;
        this.paddle = paddle;
        this.worldPowerUps = worldPowerUps;
    }

    public void activate(PowerUp powerUp) {
        if (powerUp == null) return;
        powerUp.activate(GameVar.POWERUP_ACTIVATE_DURATION);
        activePowerUps.add(powerUp);

        switch (powerUp.getType()) {
            case GameVar.ACCELERATE -> ball.setAccelerated(true);
            case GameVar.STRONGER -> ball.setStronger(true);
            case GameVar.DRUNK -> ball.setDrunk(true);
            case GameVar.BIGGERPADDLE -> paddle.setBiggerPaddle(true);
        }
    }

    @Override
    public void update(double deltaTime) {
        if (worldPowerUps != null) {
            for (PowerUp p : worldPowerUps) {
                if (p.isVisible()) {
                    p.fall(deltaTime);
                    p.updateAnimation(deltaTime);
                }
            }
        }

        Iterator<PowerUp> it = activePowerUps.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            if (!p.isActive()) {
                switch (p.getType()) {
                    case GameVar.ACCELERATE -> ball.setAccelerated(false);
                    case GameVar.STRONGER -> ball.setStronger(false);
                    case GameVar.DRUNK -> ball.setDrunk(false);
                    case GameVar.BIGGERPADDLE -> paddle.setBiggerPaddle(false);
                }
                p.deactivate();
                it.remove();
                ThePool.PowerUpPool.release(p);
            }
        }
    }

    public void reset() {
        // turn off all boolean flags on the game objects
        if (ball != null) {
            ball.setAccelerated(false);
            ball.setStronger(false);
            ball.setDrunk(false);
        }
        if (paddle != null) {
            paddle.setBiggerPaddle(false);
        }

        // clear the internal list of active power-ups
        activePowerUps.clear();
    }

    @Override
    public void clear() {
        // TODO: CLEAR
        activePowerUps.clear();
    }

    public void handleCollision(GameObject obj) {
        if (obj instanceof Paddle paddle) {
            handlePaddleCollision(paddle);
        }
    }

    public void maybeSpawnPowerUp(Brick brick) {
        if (GameRandom.nextInt(100) < GameVar.POWERUP_SPAWN_CHANCE) {
            PowerUp powerUp = new PowerUp(GameVar.powerUps[GameRandom.nextInt(GameVar.powerUps.length)]);
            powerUp.dropFrom(brick);
            worldPowerUps.add(powerUp);
        }
    }

    public List<PowerUp> getActivePowerUps() {
        return activePowerUps;
    }

    private void handlePaddleCollision(Paddle paddle) {
        if (worldPowerUps == null) return;

        List<PowerUp> toRemove = new ArrayList<>();
        for (PowerUp p : worldPowerUps) {
            if (!p.isVisible() || p.hasExpired()) {
                ThePool.PowerUpPool.release(p);
                toRemove.add(p);
                continue;
            }

            if (p.getBounds().intersects(paddle.getBounds())) {
                activate(p);
                p.setVisible(false);
                toRemove.add(p);
            }
        }
        worldPowerUps.removeAll(toRemove);
    }
}
