package com.example.demo.core;

public class Brick extends GameObject {
    private boolean destroyed;

    public Brick(int x, int y) {
        super("/images/Bricks.png", x, y); // load sprite at given position
        destroyed = false;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean val) {
        destroyed = val;
    }
}
