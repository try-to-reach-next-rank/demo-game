package com.example.demo.model.state.gameobjectdata;

import com.example.demo.model.core.gameobjects.GameObject;

public class GameObjectData{
    private double x;
    private double y;
    private double w;
    private double h;
    private boolean visible;

    public GameObjectData(GameObject<?> object) {
        this.x = object.getX();
        this.y = object.getY();
        this.w = object.getWidth();
        this.h = object.getHeight();
        this.visible = object.isVisible();
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return w; }
    public double getHeight() { return h; }
    public boolean isVisible() { return visible; }
}