package com.example.demo.model.assets;

import com.example.demo.engine.AssetLoader;
import com.example.demo.utils.var.AssetPaths;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

import java.net.URL;
import java.util.HashMap;
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
        MUSIC_ASSETS.put("Hametsu-no-Ringo", AssetPaths.MUSIC_HAMETSU_NO_RINGO);
        MUSIC_ASSETS.put("An-Impromptu-Piece", AssetPaths.MUSIC_AN_IMPROMPTU_PIECE);
        MUSIC_ASSETS.put("Gerty-on-a-Rainy-Day", AssetPaths.MUSIC_GERTY_ON_A_RAINY_DAY);
        MUSIC_ASSETS.put("You-Far-Away", AssetPaths.MUSIC_YOU_FAR_AWAY);
        MUSIC_ASSETS.put("Engraved-Star", AssetPaths.MUSIC_ENGRAVED_STAR);

        // --- Sound effects ---
        SFX_ASSETS.put("dialogue-sound", AssetPaths.SFX_DIALOGUE_SOUND);
        SFX_ASSETS.put("brick_hit", AssetPaths.SFX_BRICK_HIT);
        SFX_ASSETS.put("paddle_hit", AssetPaths.SFX_PADDLE_HIT);
        SFX_ASSETS.put("wall_hit", AssetPaths.SFX_WALL_HIT);
        SFX_ASSETS.put("game_over", AssetPaths.SFX_GAME_OVER);
        SFX_ASSETS.put("power_up", AssetPaths.SFX_POWER_UP);
        SFX_ASSETS.put("explosion_hit", AssetPaths.SFX_EXPLOSION_HIT);
        SFX_ASSETS.put("menu_selection", AssetPaths.SFX_MENU_SELECTION);
        SFX_ASSETS.put("menu_confirm", AssetPaths.SFX_MENU_CONFIRM);
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
