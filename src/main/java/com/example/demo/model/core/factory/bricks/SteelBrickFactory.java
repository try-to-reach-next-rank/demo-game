package com.example.demo.model.core.factory.bricks;

import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.bricks.SteelBrick;
import com.example.demo.model.core.factory.BrickFactory;
import com.example.demo.utils.var.GameVar;

public class SteelBrickFactory extends BrickFactory {
    @Override
    public Brick createBrick(double x, double y) {
        return new SteelBrick(x, y, GameVar.WIDTH_OF_BRICK, GameVar.HEIGHT_OF_BRICK);
    }
}
