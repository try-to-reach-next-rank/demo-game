package com.example.demo.core;

public class PowerUp extends GameObject {
    private String type;
    private boolean visible;
    private double speed = 150.0;

    public PowerUp(String type){
        super("/images/fastup.png", 0, 0);
        this.type = type;
        this.visible = false;
    }

    public void dropFrom(Brick brick) {
        this.setPosition(brick.getX(), brick.getY());
        this.visible = true;
    }

    public void update(double deltaTime) {
        if (visible) {
            y += speed * deltaTime;
            setPosition(x, y);

            if (y > VARIABLES.HEIGHT) {
                visible = false;
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getType() {
        return type;
    }
}