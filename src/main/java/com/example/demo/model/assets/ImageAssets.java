package com.example.demo.model.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.scene.image.Image;

import com.example.demo.engine.AssetLoader;
import com.example.demo.utils.var.AssetPaths;

import javafx.scene.image.Image;

public class ImageAssets implements AssetLoader {
    @Override
    public void loadInto(AssetManager manager) {
        ASSETS.forEach((key, path) ->
            manager.addImage(
                key, 
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)))
            )
        );
    }

    private static final Map<String, String> ASSETS = new HashMap<>();

    static {
        // Ball image
        ASSETS.put("ball", AssetPaths.BALL_IMAGE);
        
        // Paddle image
        ASSETS.put("paddle", AssetPaths.PADDLE_IMAGE);
        
        // Bricks images
        ASSETS.put("steel_bricks", AssetPaths.STEEL_BRICKS_IMAGE);
        ASSETS.put("bricks_1", AssetPaths.BRICKS_1_IMAGE);
        ASSETS.put("bricks_2", AssetPaths.BRICKS_2_IMAGE);
        ASSETS.put("bricks_3", AssetPaths.BRICKS_3_IMAGE);
        ASSETS.put("bricks_4", AssetPaths.BRICKS_4_IMAGE);
        ASSETS.put("bricks_5", AssetPaths.BRICKS_5_IMAGE);
        
        // Wall image
        ASSETS.put("wall_side", AssetPaths.WALL_SIDE_IMAGE);
        ASSETS.put("wall_top", AssetPaths.WALL_TOP_IMAGE);

        // Explosion sprite sheet
        ASSETS.put("explosion_spritesheet", AssetPaths.EXPLOSION_SPRITESHEET);

        // Power up sprite sheet
        ASSETS.put("powerup_spritesheet", AssetPaths.POWERUP_SPRITESHEET);
    };
}
