package com.example.demo.model.state.gameobjectdata;

import com.example.demo.model.core.gameobjects.GameObject;

public class GameObjectData {
    private double x;
    private double y;
    private boolean visible;

    public GameObjectData(GameObject<?> object) {
        this.x = object.getX();
        this.y = object.getY();
        this.visible = object.isVisible();
    }
    public double getX(){ return x; }
    public double getY(){ return y; }
    public boolean isVisible() { return visible; }
}