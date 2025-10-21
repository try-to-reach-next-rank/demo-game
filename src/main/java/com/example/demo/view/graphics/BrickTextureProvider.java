package com.example.demo.view.graphics;

import com.example.demo.model.utils.GameVar;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrickTextureProvider {
    private static final int MAX_HEALTH = GameVar.MAXHEALTH_OF_BRICKS;
    private static final List<Image> TEXTURES = new ArrayList<>();
    public static final Image TEXTURES_OF_STEELBRICK = new Image(Objects.requireNonNull(
            BrickTextureProvider.class.getResourceAsStream("/images/SteelBricks.png")));

    static {
        for (int i = 1; i <= MAX_HEALTH; i++) {
            String path = (i > 1)
                    ? "/images/Bricks" + i + ".png"
                    : "/images/Bricks.png";
            TEXTURES.add(new Image(Objects.requireNonNull(
                    BrickTextureProvider.class.getResourceAsStream(path),
                    "Missing image: " + path
            )));
        }
    }

    public static Image getTextureForHealth(int health) {
        if (health >= Integer.MAX_VALUE) return TEXTURES_OF_STEELBRICK;
        if (health < 1 || health > MAX_HEALTH) health = 1;
        return TEXTURES.get(health - 1);
    }
    public static int getMaxHealth() {
        return MAX_HEALTH;
    }
}
