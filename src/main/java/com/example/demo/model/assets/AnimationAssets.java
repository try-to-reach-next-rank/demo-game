package com.example.demo.model.assets;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.engine.AssetLoader;
import com.example.demo.utils.Animation;
import com.example.demo.utils.var.GameVar;

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
            GameVar.EXPLOSION1_EFFECT_KEY,
            AnimationData.builder(GameVar.EXPLOSION_SHEET_KEY,
                    GameVar.EXPLOSION1_FRAME_WIDTH,
                    GameVar.EXPLOSION1_FRAME_HEIGHT,
                    GameVar.EXPLOSION1_TOTAL_FRAMES)
                .loop(GameVar.EXPLOSION1_LOOP)
                .renderSize(GameVar.EXPLOSION1_RENDER_WIDTH, GameVar.EXPLOSION1_RENDER_HEIGHT)
                .rows(GameVar.EXPLOSION1_ROW)
                .duration(GameVar.EXPLOSION1_DURATION)
        );

        ASSETS.put(
            GameVar.EXPLOSION2_EFFECT_KEY,
            AnimationData.builder(GameVar.EXPLOSION_SHEET_KEY,
                    GameVar.EXPLOSION2_FRAME_WIDTH,
                    GameVar.EXPLOSION2_FRAME_HEIGHT,
                    GameVar.EXPLOSION2_TOTAL_FRAMES)
                .loop(GameVar.EXPLOSION2_LOOP)
                .renderSize(GameVar.EXPLOSION2_RENDER_WIDTH, GameVar.EXPLOSION2_RENDER_HEIGHT)
                .rows(GameVar.EXPLOSION2_ROW)
                .duration(GameVar.EXPLOSION2_DURATION)
        );

        // --- Power up spritesheet ---
        ASSETS.put(
            GameVar.POWERUP_ACC_EFFECT_KEY,
            AnimationData.builder(GameVar.POWERUP_SHEET_KEY,
                            GameVar.POWERUP_FRAME_WIDTH,
                            GameVar.POWERUP_FRAME_HEIGHT,
                            GameVar.POWERUP_TOTAL_FRAMES)
                    .loop(GameVar.POWERUP_LOOP)
                    .renderSize(GameVar.POWERUP_RENDER_WIDTH, GameVar.POWERUP_RENDER_HEIGHT)
                    .rows(GameVar.POWERUP_ACCELERATE_ROW)
                    .duration(GameVar.POWERUP_ACCELERATE_DURATION)
        );

        ASSETS.put(
            GameVar.POWERUP_STRONGER_EFFECT_KEY,
            AnimationData.builder(GameVar.POWERUP_SHEET_KEY,
                            GameVar.POWERUP_FRAME_WIDTH,
                            GameVar.POWERUP_FRAME_HEIGHT,
                            GameVar.POWERUP_TOTAL_FRAMES)
                    .loop(GameVar.POWERUP_LOOP)
                    .renderSize(GameVar.POWERUP_RENDER_WIDTH, GameVar.POWERUP_RENDER_HEIGHT)
                    .rows(GameVar.POWERUP_STRONGER_ROW)
                    .duration(GameVar.POWERUP_STRONGER_DURATION)
        );

        ASSETS.put(
            GameVar.POWERUP_STOP_TIME_EFFECT_KEY,
            AnimationData.builder(GameVar.POWERUP_SHEET_KEY,
                    GameVar.POWERUP_FRAME_WIDTH,
                    GameVar.POWERUP_FRAME_HEIGHT,
                    GameVar.POWERUP_TOTAL_FRAMES)
                .loop(GameVar.POWERUP_LOOP)
                .renderSize(GameVar.POWERUP_RENDER_WIDTH, GameVar.POWERUP_RENDER_HEIGHT)
                .rows(GameVar.POWERUP_STOPTIME_ROW)
                .duration(GameVar.POWERUP_STOPTIME_DURATION)
        );

        ASSETS.put(
            GameVar.POWERUP_BIGGER_PADDLE_EFFECT_KEY,
            AnimationData.builder(GameVar.POWERUP_SHEET_KEY,
                    GameVar.POWERUP_FRAME_WIDTH,
                    GameVar.POWERUP_FRAME_HEIGHT,
                    GameVar.POWERUP_TOTAL_FRAMES)
                .loop(GameVar.POWERUP_LOOP)
                .renderSize(GameVar.POWERUP_RENDER_WIDTH, GameVar.POWERUP_RENDER_HEIGHT)
                .rows(GameVar.POWERUP_BIGGERPADDLE_ROW)
                .duration(GameVar.POWERUP_BIGGERPADDLE_DURATION)
        );
    };

    private static class AnimationData {
        private final String    imageKey;
        private final double    frameWidth;
        private final double    frameHeight;
        private final int       totalFrames;
        private double    renderWidth;
        private double    renderHeight;
        private double    durationSeconds = 1.0; // Default Animation run 1 sec
        private int       rows            = 0;
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
    }
}
