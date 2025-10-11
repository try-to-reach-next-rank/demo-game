package com.example.demo.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.PowerUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpSystem implements Updatable {
    private final List<PowerUp> activePowerUps = new ArrayList<>();
    private final List<PowerUp> worldPowerUps;
    private final Ball ball;

    public PowerUpSystem(Ball ball, List<PowerUp> worldPowerUps) {
        this.ball = ball;
        this.worldPowerUps = worldPowerUps;
    }

    public void activate(PowerUp powerUp) {
        if (powerUp == null) return;
        powerUp.activate(5000);
        activePowerUps.add(powerUp);

        switch (powerUp.getType()) {
            case "ACCELERATE" -> ball.setAccelerated(true);
            // more types can go here (e.g. MULTI_BALL, SHIELD, etc.)
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
                    case "ACCELERATE" -> ball.setAccelerated(false);
                }
                p.deactivate();
                it.remove();
            }
        }
    }
}
