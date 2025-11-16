package com.example.demo.model.assets;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import com.example.demo.engine.AssetLoader;
import com.example.demo.utils.Animation;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

public class AssetManager {
    private static final AssetManager instance = new AssetManager();

    private final Map<String, Animation> anims  = new ConcurrentHashMap<>();
    private final Map<String, Image> images     = new ConcurrentHashMap<>();
    private final Map<String, AudioClip> sounds = new ConcurrentHashMap<>();
    private final Map<String, Media> musics     = new ConcurrentHashMap<>();
    private final Map<String, Font> fonts       = new ConcurrentHashMap<>();

    private final List<AssetLoader> loaders = List.of(
        
        new ImageAssets(),
        new AnimationAssets(),
        new SoundAssets()
        // Add more loaders here
    );

    private AssetManager() {}

    public static AssetManager getInstance() {
        return instance;
    }


    public void loadAll() {
        /**
         * Executor Service: provides a pool of treads and an API for assigning tasks to it
         *                   It is a complete solution for asynchronous processing, managing in-memory queue
         *                   and schedules submitted tasks based on thread availability
         *
         * newFixedThreadPool(): is a factory methods for Executor Service
         */
        ExecutorService executorService = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors())
                // init a thread pool of the size of the machine availableProcessors
        );

        /**
         * Future: represent the result of an asynchronous operation -> check if operation is completed or not
         */
        try {
            List<AssetLoader> baseLoaders = List.of(
                    new ImageAssets()
            );

            runandWait(executorService, baseLoaders);

            List<AssetLoader> dependentLoaders = List.of(
                    new AnimationAssets(),
                    new SoundAssets()
            );
            runandWait(executorService, dependentLoaders);
        } finally {
            executorService.shutdown();
            System.out.println("Sounds in AssetManager: " + AssetManager.getInstance().getSounds().keySet());
        }
    }

    private void runandWait(ExecutorService executorService, List<AssetLoader> loaders) {
        List<? extends Future<?>> futures = loaders.stream()
                .map(loader -> executorService.submit(() -> loader.loadInto(this)))
                .toList();

        for (Future<?> f : futures) {
            try {
                f.get(); // wait for all loaders in this group
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // -------- Accessors --------
    public Animation getOriginalAnimation(String key) {
        // Return origin animation
        return anims.get(key);
    }

    public Animation getAnimation(String key) {
        Animation original = getOriginalAnimation(key);
        if (original == null) {
            // Log here
            return null;
        }
        return original.clone();
    }

    public Image getImage(String key) {
        return images.get(key);
    }

    public AudioClip getSound(String key) {
        return sounds.get(key);
    }

    public Media getMusic(String key) {
        return musics.get(key);
    }

    // -------- Internal Putters (used by loaders) --------
    public void addAnimation(String key, Animation anim) {
        anims.put(key, anim);
    }

    public void addImage(String key, Image image) {
        images.put(key, image);
    }

    public void addSound(String key, AudioClip sound) {
        sounds.put(key, sound);
    }

    public void addMusic(String key, Media music) {
        musics.put(key, music);
    }

    public Map<String, AudioClip> getSounds() {
        return sounds;
    }

    public Map<String, Media> getMusics() {
        return musics;
    }

    public Font getFont(String name, double size) {
        String key = name + "#" + size;
        return fonts.computeIfAbsent(key, k -> new Font(name, size));
    }
}
