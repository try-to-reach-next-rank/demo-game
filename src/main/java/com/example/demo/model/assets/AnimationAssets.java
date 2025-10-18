package com.example.demo.model.assets;

import java.util.Map;
import java.util.Objects;

import com.example.demo.controller.AssetManager;
import com.example.demo.engine.AssetLoader;
import com.example.demo.model.utils.Animation;

import javafx.scene.image.Image;

public class AnimationAssets implements AssetLoader {
    @Override
    public void loadInto(AssetManager manager) {
        assets.forEach((key, data) -> {
            Image img = AssetManager.getInstance().getImage(data.imageKey);
            if (img == null) {
                System.err.println("[ERROR] Image for animation '" + key + "' not found: " + data.imageKey);
                return;
            }

            Animation anim = new Animation(
                img, data.frameWidth, data.frameHeight, data.totalFrames
            ).setLoop(data.loop)
             .setRenderSize(data.renderWidth, data.renderHeight);

            manager.addAnimation(key, anim);
        });
    }

    private static final Map<String, AnimationData> assets = Map.of(
            "explosion1", 
            new AnimationData(
                "explosion_spritesheet1", // Image Key
                96, 96,
                24,
                false,
                64, 64
        ),
            "explosion2", 
            new AnimationData(
                "explosion_spritesheet2", // Image Key
                32, 32,
                32,
                false,
                32, 32
            )
    );

    private static class AnimationData {
        private final String    imageKey;
        private final double    frameWidth;
        private final double    frameHeight;
        private final int       totalFrames;
        private final boolean   loop;
        private final double    renderWidth;
        private final double    renderHeight;

        public AnimationData(
            String imageKey,
            double frameWidth,
            double frameHeight,
            int totalFrames,
            boolean loop,
            double renderWidth,
            double renderHeight
        ) {
            this.imageKey        = imageKey;
            this.frameWidth      = frameWidth;
            this.frameHeight     = frameHeight;
            this.totalFrames     = totalFrames;
            this.loop            = loop;
            this.renderWidth     = renderWidth;
            this.renderHeight    = renderHeight;
        }
    }
}
