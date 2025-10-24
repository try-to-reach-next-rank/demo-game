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

    @Override
    public void loadInto(AssetManager manager) {
        try {
            loadMusic(manager, "Hametsu-no-Ringo", "/sounds/Hametsu-no-Ringo.mp3");
            loadMusic(manager, "An-Impromptu-Piece", "/sounds/An-Impromptu-Piece.mp3");
            loadMusic(manager, "Gerty-on-a-Rainy-Day", "/sounds/Gerty-on-a-Rainy-Day.mp3");
            loadMusic(manager, "You-Far-Away", "/sounds/You-Far-Away.mp3");
            loadMusic(manager, "Engraved-Star", "/sounds/Engraved-Star.mp3");

            loadSoundEffect(manager, "dialogue-sound", "/sounds/dialogue-sound.wav");
            loadSoundEffect(manager, "brick_hit", "/sounds/brick_hit.wav");
            loadSoundEffect(manager, "paddle_hit", "/sounds/paddle_hit.wav");
            loadSoundEffect(manager, "wall_hit", "/sounds/wall_hit.wav");
            loadSoundEffect(manager, "game_over", "/sounds/game_over.wav");
            loadSoundEffect(manager, "power_up", "/sounds/power_up.wav");
            loadSoundEffect(manager, "explosion_hit", "/sounds/explosion_hit.wav");
        } catch (Exception e) {
            System.err.println("Error when loading sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSoundEffect(AssetManager manager, String name, String path){
        URL sfxUrl = getClass().getResource(path);
        if (sfxUrl == null) System.err.println("Couldn't find SFX at path: " + path);
        else {
            AudioClip clip = new AudioClip(sfxUrl.toString());
            manager.addSound(name, clip);
        }
    }

    private void loadMusic(AssetManager manager, String name, String path){
        URL musicUrl = getClass().getResource(path);
        if (musicUrl == null) System.err.println("Couldn't find music file at path: " + path);
        else {
            Media media = new Media(musicUrl.toString());
            manager.addMusic(name,media);
        }
    }
}
