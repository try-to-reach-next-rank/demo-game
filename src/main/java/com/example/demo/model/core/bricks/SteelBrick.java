package com.example.demo.model.core.bricks;

import com.example.demo.view.graphics.BrickTextureProvider;

public class SteelBrick extends Brick {
    public SteelBrick(double x, double y, double width, double height) {
        super(Integer.MAX_VALUE, BrickTextureProvider.getTextureForHealth(Integer.MAX_VALUE), x, y, width, height);
    }

    @Override
    public boolean takeDamage(int damage) {
        return false;
    }

    @Override
    public int getScoreValue() {
        return 0;
    }

    @Override
    public String getSound() {
        return "steel_hit";
    }
}
