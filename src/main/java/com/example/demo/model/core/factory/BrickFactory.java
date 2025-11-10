package com.example.demo.model.core.factory;

import com.example.demo.model.core.entities.Brick;
import com.example.demo.model.core.builder.BrickBuilder;
import com.example.demo.utils.GameRandom;
import com.example.demo.view.graphics.BrickTextureProvider;

public class BrickFactory {
    /**
     * Brick Factory decides brick properties / init logic
     * @param health health to decide the type
     * @param x position x
     * @param y position y
     * @return Brick
     */
    public static Brick createFromType(int health, double x, double y) {
        String imageKey = BrickTextureProvider.getTextureForHealth(health);

        return new BrickBuilder()
                .imageKey(imageKey)
                .position(x, y)
                .health(health)
                .destroyed(false)
                .build();
    }
}
