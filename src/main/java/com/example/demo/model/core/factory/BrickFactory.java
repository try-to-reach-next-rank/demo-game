package com.example.demo.model.core.factory;

import com.example.demo.model.core.bricks.Brick;

public abstract class BrickFactory {
    public abstract Brick createBrick(double x, double y);
}
