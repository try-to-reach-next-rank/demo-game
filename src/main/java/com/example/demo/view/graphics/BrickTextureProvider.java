package com.example.demo.view.graphics;

import com.example.demo.model.utils.GameVar;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrickTextureProvider {
    private static final int MAX_HEALTH = GameVar.MAXHEALTH_OF_BRICKS;
    private static final List<String> IMAGEKEYS = new ArrayList<>();

    static {
        for (int i = 1; i <= MAX_HEALTH; i++) {
            String path = "bricks_" + i;
            IMAGEKEYS.add(path);
        }
    }

    public static String getTextureForHealth(int health) {
        if (health >= Integer.MAX_VALUE) return "steel_bricks";
        if (health < 1 || health > MAX_HEALTH) health = 1;
        return IMAGEKEYS.get(health - 1);
    }

    public static int getMaxHealth() {
        return MAX_HEALTH;
    }
}
