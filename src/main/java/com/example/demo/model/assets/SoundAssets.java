package com.example.demo.model.assets;

import com.example.demo.controller.AssetManager;
import com.example.demo.engine.AssetLoader;
import com.example.demo.model.utils.Sound;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundAssets implements AssetLoader {
    private enum SoundType { MUSIC, SFX }
    private static final Map<String, String> MUSIC_ASSETS = new HashMap<>();
    private static final Map<String, String> SFX_ASSETS   = new HashMap<>();

    @Override
    public void loadInto(AssetManager manager) {
        loadAssets(manager, MUSIC_ASSETS, SoundType.MUSIC);
        loadAssets(manager, SFX_ASSETS, SoundType.SFX);
    }

    static {
        // --- Music ---
        MUSIC_ASSETS.put("Hametsu-no-Ringo", "/sounds/Hametsu-no-Ringo.mp3");
        MUSIC_ASSETS.put("An-Impromptu-Piece", "/sounds/An-Impromptu-Piece.mp3");
        MUSIC_ASSETS.put("Gerty-on-a-Rainy-Day", "/sounds/Gerty-on-a-Rainy-Day.mp3");
        MUSIC_ASSETS.put("You-Far-Away", "/sounds/You-Far-Away.mp3");
        MUSIC_ASSETS.put("Engraved-Star", "/sounds/Engraved-Star.mp3");

        // --- Sound effects ---
        SFX_ASSETS.put("dialogue-sound", "/sounds/dialogue-sound.wav");
        SFX_ASSETS.put("brick_hit", "/sounds/brick_hit.wav");
        SFX_ASSETS.put("paddle_hit", "/sounds/paddle_hit.wav");
        SFX_ASSETS.put("wall_hit", "/sounds/wall_hit.wav");
        SFX_ASSETS.put("game_over", "/sounds/game_over.wav");
        SFX_ASSETS.put("power_up", "/sounds/power_up.wav");
        SFX_ASSETS.put("explosion_hit", "/sounds/explosion_hit.wav");
    }

    private void loadAssets(AssetManager manager, Map<String, String> assets, SoundType type) {
        assets.forEach((name, path) -> {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("[WARN] Missing sound asset: " + path);
                return;
            }

            switch (type) {
                case MUSIC -> manager.addMusic(name, new Media(url.toString()));
                case SFX -> manager.addSound(name, new AudioClip(url.toString()));
            }
        });
    }
}
