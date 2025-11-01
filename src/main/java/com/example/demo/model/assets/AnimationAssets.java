package com.example.demo.model.assets;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.controller.view.AssetManager;
import com.example.demo.engine.AssetLoader;
import com.example.demo.utils.Animation;

import javafx.scene.image.Image;

public class AnimationAssets implements AssetLoader {
    @Override
    public void loadInto(AssetManager manager) {
        ASSETS.forEach((key, data) -> {
            Image img = AssetManager.getInstance().getImage(data.imageKey);
            if (img == null) {
                System.err.println("[ERROR] Image for animation '" + key + "' not found: " + data.imageKey);
                return;
            }

            Animation anim = new Animation(
                img, data.frameWidth, data.frameHeight, data.totalFrames
            ).setLoop(data.loop)
             .setRenderSize(data.renderWidth, data.renderHeight)
             .setRows(data.rows)
             .setDuration(data.durationSeconds);

            manager.addAnimation(key, anim);
        });
    }

    private static final Map<String, AnimationData> ASSETS = new HashMap<>();
    
    static {
        // --- Explosion spritesheet ---
        ASSETS.put(
            "explosion1", 
            AnimationData.builder("explosion_spritesheet", 96, 96, 24)
                .loop(false)
                .renderSize(64, 64)
                .rows(0)
        );

        ASSETS.put(
            "explosion2", 
            AnimationData.builder("explosion_spritesheet", 96, 96, 32)
                .loop(false)
                .renderSize(32, 32)
                .rows(1)
        );

        // --- Power up spritesheet ---
        ASSETS.put(
            "powerup_accelerate", 
            AnimationData.builder("powerup_spritesheet", 32, 16, 8)
                .loop(true)
                .renderSize(32, 16)
                .rows(0)
                .duration(5.0)
        );

        ASSETS.put(
            "powerup_stronger", 
            AnimationData.builder("powerup_spritesheet", 32, 16, 8)
                .loop(true)
                .renderSize(32, 16)
                .rows(1)
                .duration(2.0)
        );

        ASSETS.put(
            "powerup_stoptime", 
            AnimationData.builder("powerup_spritesheet", 32, 16, 8)
                .loop(true)
                .renderSize(32, 16)
                .rows(2)
                .duration(10.0)
        );

        ASSETS.put(
            "powerup_biggerpaddle", 
            AnimationData.builder("powerup_spritesheet", 32, 16, 8)
                .loop(true)
                .renderSize(32, 16)
                .rows(3)
                .duration(1.0)
        );
    };

    private static class AnimationData {
        private String    imageKey;
        private double    frameWidth;
        private double    frameHeight;
        private int       totalFrames;
        private double    renderWidth;
        private double    renderHeight;
        private double    durationSeconds = 1.0; // Default Animation run 1 sec
        private int       rows            = 0;
        private int       startFrameIndex = 0;
        private boolean   loop            = false;

        private AnimationData(String imageKey, double frameWidth, double frameHeight, int totalFrames) {
            this.imageKey        = imageKey;
            this.frameWidth      = frameWidth;
            this.frameHeight     = frameHeight;
            this.totalFrames     = totalFrames;
        }

        private static AnimationData builder(String imageKey, double frameWidth, double frameHeight, int totalFrames) {
            return new AnimationData(imageKey, frameWidth, frameHeight, totalFrames);
        }

        private AnimationData loop(boolean loop) {
            this.loop = loop;
            return this;
        } 

        private AnimationData renderSize(double renderWidth, double renderHeight) {
            this.renderWidth = renderWidth;
            this.renderHeight = renderHeight;
            return this;
        }

        private AnimationData rows(int rows) {
            this.rows = rows;
            return this;
        }

        private AnimationData duration(double durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        private AnimationData startFrame(int index) {
            this.startFrameIndex = index;
            return this;
        }
    }
}
