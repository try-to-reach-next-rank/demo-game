package com.example.demo.model.core.factory.bricks;

import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.bricks.NormalBrick;
import com.example.demo.model.core.factory.BrickFactory;
import com.example.demo.view.graphics.BrickTextureProvider;

public class NormalBrickFactory extends BrickFactory {
    private final int health;
    private final double width;
    private final double height;

    public NormalBrickFactory(int health, double width, double height) {
        this.health = health;
        this.width = width;
        this.height = height;
    }

    @Override
    public Brick createBrick(double x, double y) {
        String imageKey = BrickTextureProvider.getTextureForHealth(health);
        return new NormalBrick(health, imageKey, x, y, width, height);
    }
}
