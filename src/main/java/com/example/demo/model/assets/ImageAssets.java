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
        assets.forEach((key, path) ->
            manager.addImage(
                key, 
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)))
            )
        );
    }

    private static final Map<String, String> assets = new HashMap<>();

    static {
        assets.put("ball", "/images/Ball.png");
        assets.put("paddle", "/images/Paddle.png");
        assets.put("bricks_1", "/images/Bricks.png");
        assets.put("bricks_2", "/images/Bricks2.png");
        assets.put("bricks_3", "/images/Bricks3.png");
        assets.put("bricks_4", "/images/Bricks4.png");
        assets.put("bricks_5", "/images/Bricks5.png");
        assets.put("wall", "/images/Wall.png");
        assets.put("powerup", "/images/fastup.png");
        assets.put("explosion_spritesheet1", "/images/explosion/explosion1.png");
        assets.put("explosion_spritesheet2", "/images/explosion/explosion2.png");
    };
}
