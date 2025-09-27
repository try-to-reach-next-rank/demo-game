package com.example.demo.core;

public class PowerUp extends GameObject {
    private String type;
    private boolean visible;

    public PowerUp(){
        super("/images/fastup.png", 0, 0);
        this.visible = false;
    }

    public void dropFrom(Brick brick) {
        this.setPosition(brick.getX(), brick.getY());
        this.visible = true;
    }

    public void move(){
        if (visible) {
            y += VARIABLES.SPEED * 2.0F;
            setPosition(x, y);
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