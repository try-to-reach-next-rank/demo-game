package com.example.demo.controller.core;

import javafx.scene.image.Image;
import java.util.Objects;

public class SteelBrick extends Brick {
    private static final Image STEEL_TEXTURE;

    static {
        STEEL_TEXTURE = new Image(Objects.requireNonNull(
                SteelBrick.class.getResourceAsStream("/images/SteelBricks.png"),
                "Missing image file: /images/SteelBricks.png"));
    }

    public SteelBrick(int x, int y) {
        super(STEEL_TEXTURE, x, y);
    }

    @Override
    public String takeDamage() {

        return "wall_hit";// Return the sound for a steel hit
    }
}
