package com.example.demo.model.assets;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.demo.engine.AssetLoader;
import com.example.demo.utils.Animation;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

public class AssetManager {
    private static final AssetManager instance = new AssetManager();

    private final Map<String, Animation> anims = new ConcurrentHashMap<>();
    private final Map<String, Image> images = new ConcurrentHashMap<>();
    private final Map<String, AudioClip> sounds = new ConcurrentHashMap<>();
    private final Map<String, Media> musics = new ConcurrentHashMap<>();
    private final Map<String, Font> fonts = new ConcurrentHashMap<>();

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
            Runtime.getRuntime().availableProcessors()
                // init a thread pool of the size of the machine availableProcessors
        );

        /**
         * Future: represent the result of an asynchronous operation -> check if operation is completed or not
         */
        try {
            List<Callable<Void>> tasks = loaders.stream()
                    .<Callable<Void>>map(loader -> () -> {
                        loader.loadInto(this);
                        return null;
                    })
                    .toList();

            /**
             * Execute the given tasks, returning a list of Future holding result and status
             */
            executorService.invokeAll(tasks);

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
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

    public Font getFont(String name, int size) {
        String key = name + "#" + size;
        return fonts.computeIfAbsent(key, k -> new Font(name, size));
    }
}
