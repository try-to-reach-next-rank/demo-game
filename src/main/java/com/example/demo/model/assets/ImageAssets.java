package com.example.demo.model.assets;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.scene.image.Image;

import com.example.demo.engine.AssetLoader;
import com.example.demo.utils.var.AssetPaths;

public class ImageAssets implements AssetLoader {
    @Override
    public void loadInto(AssetManager manager) {
        ASSETS.forEach((key, path) -> {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) System.err.println("Cannot find resource: " + path);
            else manager.addImage(key, new Image(is));
        });
    }

    private static final Map<String, String> ASSETS = new HashMap<>();

    static {
        // Default
        ASSETS.put("default", AssetPaths.DEFAULT_IMAGE);

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

        // Moved wall image
        ASSETS.put("moved_wall", AssetPaths.MOVED_WALL_IMAGE);

        // Portal sprite sheet
        ASSETS.put("portal_spritesheet", AssetPaths.PORTAL_SPRITESHEET);

        // Explosion sprite sheet
        ASSETS.put("explosion_spritesheet", AssetPaths.EXPLOSION_SPRITESHEET);

        // Power up sprite sheet
        ASSETS.put("powerup_spritesheet", AssetPaths.POWERUP_SPRITESHEET);

        ASSETS.put("hand_open", AssetPaths.HAND_OPEN);
        ASSETS.put("hand_punch", AssetPaths.HAND_PUNCH);
        ASSETS.put("cloud_left", AssetPaths.CLOUD_LEFT);
        ASSETS.put("cloud_right", AssetPaths.CLOUD_RIGHT);
    };
}
