package com.example.demo.model.state;

import com.example.demo.model.core.PowerUp;

public class PowerUpData {
    private double x;
    private double y;
    private String type;
    private boolean visible;

    public PowerUpData() {}

    public PowerUpData(PowerUp powerUp) {
        this.x = powerUp.getX();
        this.y = powerUp.getY();
        this.type = powerUp.getType();
        this.visible = powerUp.isVisible();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public String getType() { return type; }
    public boolean isVisible() { return visible; }
}