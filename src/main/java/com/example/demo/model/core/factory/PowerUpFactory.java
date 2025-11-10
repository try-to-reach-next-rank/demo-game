package com.example.demo.model.core.factory;

import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.builder.PowerUpBuilder;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.var.GameVar;

public class PowerUpFactory {
    public static PowerUp createRandomPowerUp(double x, double y) {
        String type = GameVar.powerUps[GameRandom.nextInt(GameVar.powerUps.length)];
        return new PowerUpBuilder()
                .type(type)
                .position(x, y)
                .visible(true)
                .build();
    }
}
