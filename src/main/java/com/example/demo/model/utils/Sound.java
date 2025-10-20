package com.example.demo.model.utils;

import com.example.demo.model.menu.SettingsModel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.*;
import java.net.URL;

public class Sound {
    // Singleton instance
    private static final Sound instance = new Sound();

    // Music management
    private final Map<String, Media> musicLibrary = new HashMap<>();
    private final List<String> musicKey = new ArrayList<>();
    private int currentTrackIndex = 0;
    private MediaPlayer currentMusicPlayer;

    // Sound effects management
    private final Map<String, AudioClip> soundEffects = new HashMap<>();

    // Settings integration - THÊM CÁC FIELD NÀY
    private SettingsModel settings;
    private double musicVolume = 0.5;
    private double effectVolume = 1.0;
    private boolean musicEnabled = true;
    private boolean effectEnabled = true;

    private Sound() {
        loadSounds();
    }

    private void loadSounds(){
        try{
            loadMusic("Hametsu-no-Ringo", "/sounds/Hametsu-no-Ringo.mp3");
            loadMusic("An-Impromptu-Piece", "/sounds/An-Impromptu-Piece.mp3");
            loadMusic("Gerty-on-a-Rainy-Day", "/sounds/Gerty-on-a-Rainy-Day.mp3");
            loadMusic("You-Far-Away", "/sounds/You-Far-Away.mp3");
            loadMusic("Engraved-Star", "/sounds/Engraved-Star.mp3");

            loadSoundEffect("dialogue-sound", "/sounds/dialogue-sound.wav");
            loadSoundEffect("brick_hit", "/sounds/brick_hit.wav");
            loadSoundEffect("paddle_hit", "/sounds/paddle_hit.wav");
            loadSoundEffect("wall_hit", "/sounds/wall_hit.wav");
            loadSoundEffect("game_over", "/sounds/game_over.wav");
            loadSoundEffect("power_up","/sounds/power_up.wav");
            loadSoundEffect("explosion_hit","/sounds/explosion_hit.wav");
        }
        catch (Exception e){
            System.err.println("Error when loading sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSoundEffect(String name, String path){
        URL sfxUrl = getClass().getResource(path);
        if (sfxUrl == null) System.err.println("Couldn't find SFX at path: " + path);
        else {
            AudioClip clip = new AudioClip(sfxUrl.toString());
            soundEffects.put(name, clip);
        }
    }

    private void loadMusic(String name, String path){
        URL musicUrl = getClass().getResource(path);
        if (musicUrl == null) System.err.println("Couldn't find music file at path: " + path);
        else {
            Media media = new Media(musicUrl.toString());
            musicLibrary.put(name, media);
            musicKey.add(name);
        }
    }

    public static Sound getInstance(){
        return instance;
    }

    // ============ SETTINGS INTEGRATION - THÊM CÁC METHOD NÀY ============

    /**
     * Kết nối với SettingsModel để đồng bộ volume và enable/disable
     * Được gọi tự động từ SettingsModel constructor
     */
    public void bindSettings(SettingsModel settings) {
        this.settings = settings;

        // Đồng bộ giá trị ban đầu
        this.musicVolume = settings.getMusicVolume();
        this.effectVolume = settings.getEffectVolume();
        this.musicEnabled = settings.isMusicEnabled();
        this.effectEnabled = settings.isEffectEnabled();

        // Áp dụng ngay lập tức
        applyMusicVolume();
        applyEffectVolume();

        // Listeners để tự động cập nhật khi settings thay đổi
        settings.musicVolumeProperty().addListener((obs, oldVal, newVal) -> {
            this.musicVolume = newVal.doubleValue();
            applyMusicVolume();
        });

        settings.effectVolumeProperty().addListener((obs, oldVal, newVal) -> {
            this.effectVolume = newVal.doubleValue();
            applyEffectVolume();
        });

        settings.musicEnabledProperty().addListener((obs, oldVal, newVal) -> {
            this.musicEnabled = newVal;
            applyMusicVolume();
        });

        settings.effectEnabledProperty().addListener((obs, oldVal, newVal) -> {
            this.effectEnabled = newVal;
            applyEffectVolume();
        });
    }

    /**
     * Áp dụng music volume vào MediaPlayer hiện tại
     */
    private void applyMusicVolume() {
        if (currentMusicPlayer != null) {
            currentMusicPlayer.setVolume(musicEnabled ? musicVolume : 0.0);
        }
    }

    /**
     * Áp dụng effect volume vào tất cả AudioClips
     */
    private void applyEffectVolume() {
        double volume = effectEnabled ? effectVolume : 0.0;
        for (AudioClip clip : soundEffects.values()) {
            clip.setVolume(volume);
        }
    }

    // ============ MUSIC METHODS - SỬA CÁC METHOD NÀY ============

    public void playMusic(String name) {
        stopMusic();

        Media media = musicLibrary.get(name);
        if (media == null) {
            System.err.println("Couldn't find music with name: " + name);
            return;
        }

        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setVolume(musicEnabled ? musicVolume : 0.0); // SỬA: thay 0.5
        currentMusicPlayer.setOnEndOfMedia(() -> playNextMusic());
        currentMusicPlayer.play();

        currentTrackIndex = musicKey.indexOf(name);
    }

    public void loopMusic(String name) {
        stopMusic();

        Media media = musicLibrary.get(name);
        if (media == null) {
            System.err.println("Couldn't find music to loop with name: " + name);
            return;
        }

        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setVolume(musicEnabled ? musicVolume : 0.0); // SỬA: thay 0.5
        currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        currentMusicPlayer.play();

        currentTrackIndex = musicKey.indexOf(name);
    }

    public void stopMusic(){
        if(currentMusicPlayer != null) {
            currentMusicPlayer.stop();
            currentMusicPlayer.dispose(); // THÊM: tránh memory leak
            currentMusicPlayer = null;
        }
    }

    // THÊM METHOD MỚI
    public void pauseMusic() {
        if(currentMusicPlayer != null) {
            currentMusicPlayer.pause();
        }
    }

    // THÊM METHOD MỚI
    public void resumeMusic() {
        if(currentMusicPlayer != null && musicEnabled) {
            currentMusicPlayer.play();
        }
    }

    /**
     * @deprecated Use SettingsModel to control volume instead
     */
    @Deprecated
    public void setMusicVolume(double volume) {
        this.musicVolume = volume;
        applyMusicVolume();
    }

    public void playNextMusic(){
        if(musicKey.isEmpty()) return;

        currentTrackIndex = (currentTrackIndex + 1) % musicKey.size();
        String nextTrack = musicKey.get(currentTrackIndex);
        playMusic(nextTrack);
    }

    public void playRandomMusic(){
        if(musicKey.isEmpty()) return;

        Collections.shuffle(musicKey);
        currentTrackIndex = 0;
        playMusic(musicKey.get(currentTrackIndex));
    }

    // ============ SOUND EFFECT METHODS - SỬA CÁC METHOD NÀY ============

    public void playSound(String name){
        if (!effectEnabled) return; // THÊM: check enable

        AudioClip clip = soundEffects.get(name);
        if (clip == null) {
            System.err.println("Couldn't find sfx according to its name: " + name);
        } else {
            clip.setVolume(effectVolume); // THÊM: set volume
            clip.play();
        }
    }

    public void loopSound(String name) {
        if (!effectEnabled) return; // THÊM: check enable

        AudioClip clip = soundEffects.get(name);
        if (clip == null) {
            System.err.println("Couldn't find sfx to loop with name: " + name);
            return;
        }

        clip.setVolume(effectVolume); // THÊM: set volume
        clip.setCycleCount(AudioClip.INDEFINITE);
        clip.play();
    }

    public void stopSound(String name) {
        AudioClip clip = soundEffects.get(name);
        if (clip != null) {
            clip.stop();
            clip.setCycleCount(1);
        } else {
            System.err.println("Couldn't find sfx to stop with name: " + name);
        }
    }

    // THÊM METHOD MỚI (OPTIONAL - hữu ích khi cleanup)
    public void stopAllSounds() {
        for (AudioClip clip : soundEffects.values()) {
            clip.stop();
            clip.setCycleCount(1);
        }
    }

    // ============ GETTERS (OPTIONAL - để debug) ============

    public double getMusicVolume() {
        return musicVolume;
    }

    public double getEffectVolume() {
        return effectVolume;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public boolean isEffectEnabled() {
        return effectEnabled;
    }

    public String getCurrentTrackName() {
        if (currentTrackIndex >= 0 && currentTrackIndex < musicKey.size()) {
            return musicKey.get(currentTrackIndex);
        }
        return null;
    }
}