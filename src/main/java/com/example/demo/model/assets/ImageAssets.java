package com.example.demo.model.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.scene.image.Image;

import com.example.demo.controller.AssetManager;
import com.example.demo.engine.AssetLoader;

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
        // --- Images ---
        // Ball image
        ASSETS.put("ball", "/images/Ball.png");
        
        // Paddle image
        ASSETS.put("paddle", "/images/Paddle.png");
        
        // Bricks images
        ASSETS.put("steel_bricks", "/images/SteelBricks.png");
        ASSETS.put("bricks_1", "/images/Bricks.png");
        ASSETS.put("bricks_2", "/images/Bricks2.png");
        ASSETS.put("bricks_3", "/images/Bricks3.png");
        ASSETS.put("bricks_4", "/images/Bricks4.png");
        ASSETS.put("bricks_5", "/images/Bricks5.png");
        
        // Wall image
        // ASSETS.put("wall", "/images/Wall.png");
        ASSETS.put("wall_sidep", "/images/wall/Wall.png");
        ASSETS.put("wall_top", "/images/wall/WallRotated.png");
        
        // Power up image // TODO: Delete this later
        ASSETS.put("powerup", "/images/fastup.png");
        
        // --- Sprite sheet ---
        // Explosion sprite sheet
        ASSETS.put("explosion_spritesheet", "/images/explosion/explosion.png");

        // Power up sprite sheet
        ASSETS.put("powerup_spritesheet", "/images/asset/powerups.png");
    };
}
