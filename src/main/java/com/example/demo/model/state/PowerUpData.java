package com.example.demo.model.state;

import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.state.gameobjectdata.AnimatedObjectData;
import com.example.demo.model.state.gameobjectdata.GameObjectData;

public class PowerUpData extends AnimatedObjectData {
    private String type;
    private boolean visible;

    public PowerUpData(PowerUp powerUp) {
        super(powerUp);
        this.type = powerUp.getType();
        this.visible = powerUp.isVisible();
    }

    public String getType() { return type; }
    public boolean isVisible() { return visible; }
}