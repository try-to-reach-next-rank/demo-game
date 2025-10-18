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

    private static final Map<String, String> assets = Map.of(
        "ball", "/images/Ball.png",
        "explosion_spritesheet1", "/images/explosion/explosion1.png",
        "explosion_spritesheet2", "/images/explosion/explosion2.png"
    );
}
