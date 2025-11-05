package com.example.demo.model.state;

import com.example.demo.model.core.PowerUp;
import com.example.demo.model.state.gameobjectdata.AnimationedObjectData;
import com.example.demo.model.state.gameobjectdata.GameObjectData;

public class PowerUpData extends AnimationedObjectData {
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