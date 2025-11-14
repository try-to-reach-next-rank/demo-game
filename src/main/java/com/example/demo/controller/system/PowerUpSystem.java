package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.PowerUp;
import com.example.demo.model.core.ThePool;
import com.example.demo.model.state.ActivePowerUpData;
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

    public void activateFromSave(ActivePowerUpData data) {
        // 1. Create a new PowerUp object based on the saved type
        PowerUp powerUp = new PowerUp(data.getType());

        // 2. Activate the powerUps with the saved remaining time (in milliseconds)
        powerUp.activateWithRemainingDuration(data.getRemainingDuration());

        // 3. Add this newly re-created power-up to the live list of active power-ups
        activePowerUps.add(powerUp);

        // 4. Re-apply the actual game effect
        //    It checks the type and tells the ball or paddle to change its state.
        switch (powerUp.getType()) {
            case GameVar.ACCELERATE: ball.setAccelerated(true); break;
            case GameVar.STRONGER: ball.setStronger(true); break;
            case GameVar.DRUNK: ball.setDrunk(true); break;
            case GameVar.BIGGERPADDLE: paddle.setBiggerPaddle(true); break;
        }
    }

    public List<PowerUp> getActivePowerUps() {
        return activePowerUps;
    }
}
