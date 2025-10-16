package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.PowerUp;
import com.example.demo.model.utils.GameVar;

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
        powerUp.activate(5000);
        activePowerUps.add(powerUp);

        switch (powerUp.getType()) {
            case GameVar.ACCELERATE -> ball.setAccelerated(true);
            case GameVar.STRONGER -> ball.setStronger(true);
            case GameVar.STOPTIME -> ball.setStopTime(true);
            case GameVar.BIGGERPADDLE -> paddle.setBiggerPaddle(true);
         }
    }

    @Override
    public void update(double deltaTime) {
        if (worldPowerUps != null) {
            for (PowerUp p : worldPowerUps) {
                if (p.isVisible()) {
                    p.fall(deltaTime);
                }
            }
        }

        Iterator<PowerUp> it = activePowerUps.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            if (p.hasExpired()) {
                switch (p.getType()) {
                    case GameVar.ACCELERATE -> ball.setAccelerated(false);
                    case GameVar.STRONGER -> ball.setStronger(false);
                    case GameVar.STOPTIME -> ball.setStopTime(false);
                    case GameVar.BIGGERPADDLE -> paddle.setBiggerPaddle(false);
                }
                p.deactivate();
                it.remove();
            }
        }
    }
}
