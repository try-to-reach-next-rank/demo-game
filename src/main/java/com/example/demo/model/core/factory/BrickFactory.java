package com.example.demo.model.core.factory;

import com.example.demo.model.core.Brick;
import com.example.demo.model.core.builder.BrickBuilder;
import com.example.demo.utils.GameRandom;
import com.example.demo.view.graphics.BrickTextureProvider;

public class BrickFactory {
    /**
     * Brick Factory decides brick properties / init logic
     * @param type type of brick
     * @param x position x
     * @param y position y
     * @return Brick
     */
    public static Brick createFromType(int type, double x, double y) {
        int health = (type == 2) ? Integer.MAX_VALUE : (GameRandom.nextInt(5) + 1);
        String imageKey = BrickTextureProvider.getTextureForHealth(health);

        return new BrickBuilder()
                .imageKey(imageKey)
                .position(x, y)
                .health(health)
                .destroyed(false)
                .build();
    }
}
