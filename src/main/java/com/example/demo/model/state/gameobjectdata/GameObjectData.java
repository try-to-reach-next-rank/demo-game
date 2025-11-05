package com.example.demo.model.state.gameobjectdata;

import com.example.demo.model.core.gameobjects.GameObject;

public class GameObjectData {
    public double x;
    public double y;

    public GameObjectData(GameObject object) {
        this.x = object.getX();
        this.y = object.getY();
    }
    public double getX(){ return x; }
    public double getY(){ return y; }
}