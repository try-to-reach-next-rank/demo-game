package com.example.demo.model.state.gameobjectdata;

import com.example.demo.model.core.gameobjects.GameObject;

public class GameObjectData{
    public double x;
    public double y;
    public double w;
    public double h;

    public GameObjectData(GameObject object){
        this.x = object.getX();
        this.y = object.getY();
        this.w = object.getWidth();
        this.h = object.getHeight();
    }
    public double getX(){ return x; }
    public double getY(){ return y; }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }
}