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
             .setRenderSize(data.renderWidth, data.renderHeight)
             .setRows(data.rows);

            manager.addAnimation(key, anim);
        });
    }

    private static final Map<String, AnimationData> assets = Map.of(
        // --- Explosion spritesheet ---
        "explosion1", 
        AnimationData.builder("explosion_spritesheet1", 96, 96, 24)
            .loop(false)
            .renderSize(64, 64)
            .rows(0),

        "explosion2", 
        AnimationData.builder("explosion_spritesheet2", 32, 32, 32)
            .loop(false)
            .renderSize(32, 32)
            .rows(0),

        // --- Power up spritesheet ---
        // "test", 
        // AnimationData.builder("powerup_spritesheet", 46, 21, 8)
        //     .loop(true)
        //     .renderSize(46, 21)
        //     .rows(1),

        "powerup_accelerate", 
        AnimationData.builder("powerup_spritesheet", 46, 21, 8)
            .loop(true)
            .renderSize(46, 21)
            .rows(0),

        "powerup_stronger", 
        AnimationData.builder("powerup_spritesheet", 46, 21, 8)
            .loop(true)
            .renderSize(46, 21)
            .rows(1),

        "powerup_stoptime", 
        AnimationData.builder("powerup_spritesheet", 46, 21, 8)
            .loop(true)
            .renderSize(46, 21)
            .rows(2),

        "powerup_biggerpaddle", 
        AnimationData.builder("powerup_spritesheet", 46, 21, 8)
            .loop(true)
            .renderSize(46, 21)
            .rows(2),

        "phuc", 
        AnimationData.builder("powerup", 32, 32, 1)
            .loop(true)
            .renderSize(10, 10)
    );

    private static class AnimationData {
        private String    imageKey;
        private double    frameWidth;
        private double    frameHeight;
        private int       totalFrames;
        private double    renderWidth;
        private double    renderHeight;
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

        private AnimationData startFrame(int index) {
            this.startFrameIndex = index;
            return this;
        }
    }
}
