package com.example.demo.model.state;

import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.state.gameobjectdata.AnimatedObjectData;

public class PowerUpData extends AnimatedObjectData {
    private String type;

    public PowerUpData(PowerUp powerUp) {
        super(powerUp);
        this.type = powerUp.getType();
    }

    public String getType() { return type; }
}