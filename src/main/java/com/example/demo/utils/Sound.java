package com.example.demo.utils;

import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.menu.SettingsModel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.*;

public class Sound {
    // Singleton instance
    private static final Sound instance = new Sound();

    private int currentTrackIndex = 0;
    private MediaPlayer currentMusicPlayer;

    // Settings integration - THÊM CÁC FIELD NÀY
    private SettingsModel settings;
    private double musicVolume = 0.5;
    private double effectVolume = 1.0;
    private boolean musicEnabled = true;
    private boolean effectEnabled = true;

    private Sound() {
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
        for (AudioClip clip : AssetManager.getInstance().getSounds().values()) {
            clip.setVolume(volume);
        }
    }

    // ============ MUSIC METHODS - SỬA CÁC METHOD NÀY ============

    public void playMusic(String name) {
        stopMusic();

        Media media = AssetManager.getInstance().getMusic(name);
        if (media == null) {
            System.err.println("Couldn't find music with name: " + name);
            return;
        }

        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setVolume(musicEnabled ? musicVolume : 0.0); // SỬA: thay 0.5
        currentMusicPlayer.setOnEndOfMedia(() -> playNextMusic());
        currentMusicPlayer.play();

        currentTrackIndex = new ArrayList<>(AssetManager.getInstance().getMusics().keySet()).indexOf(name);
    }

    public void playMusic(String name, double timeInMilliseconds) {
        if (name == null || name.isEmpty()) {
            playRandomMusic();
            return;
        }

        stopMusic();

        Media media = AssetManager.getInstance().getMusic(name);
        if (media == null) {
            System.err.println("Couldn't find music with name: " + name);
           // playRandomMusic();
            return;
        }

        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setVolume(musicEnabled ? musicVolume : 0.0); // SỬA: thay 0.5
        currentMusicPlayer.setOnEndOfMedia(() -> playNextMusic());

        currentMusicPlayer.setOnReady(() -> {
            if (timeInMilliseconds > 0) {
                currentMusicPlayer.seek(Duration.millis(timeInMilliseconds));
            }
            currentMusicPlayer.play();
        });

        // Cập nhật lại trackIndex
        this.currentTrackIndex = new ArrayList<>(AssetManager.getInstance().getMusics().keySet()).indexOf(name);
    }

    public void loopMusic(String name) {
        stopMusic();

        Media media = AssetManager.getInstance().getMusic(name);
        if (media == null) {
            System.err.println("Couldn't find music to loop with name: " + name);
            return;
        }

        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setVolume(musicEnabled ? musicVolume : 0.0); // SỬA: thay 0.5
        currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        currentMusicPlayer.play();

        currentTrackIndex = new ArrayList<>(AssetManager.getInstance().getMusics().keySet()).indexOf(name);
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
        List<String> musicKey = new ArrayList<>(AssetManager.getInstance().getMusics().keySet());
        if(musicKey.isEmpty()) return;

        currentTrackIndex = (currentTrackIndex + 1) % musicKey.size();
        String nextTrack = musicKey.get(currentTrackIndex);
        playMusic(nextTrack);
    }

    public void playRandomMusic(){
        List<String> musicKey = new ArrayList<>(AssetManager.getInstance().getMusics().keySet());
        if(musicKey.isEmpty()) return;

        Collections.shuffle(musicKey);
        currentTrackIndex = 0;
        playMusic(musicKey.get(currentTrackIndex));
    }

    // ============ SOUND EFFECT METHODS - SỬA CÁC METHOD NÀY ============

    public void playSound(String name){
        if (!effectEnabled) return; // THÊM: check enable

        AudioClip clip = AssetManager.getInstance().getSound(name);
        if (clip == null) {
            System.err.println("Couldn't find sfx according to its name: " + name);
        } else {
            clip.setVolume(effectVolume); // THÊM: set volume
            clip.play();
        }
    }

    public void loopSound(String name) {
        if (!effectEnabled) return; // THÊM: check enable

        AudioClip clip = AssetManager.getInstance().getSound(name);
        if (clip == null) {
            System.err.println("Couldn't find sfx to loop with name: " + name);
            return;
        }

        clip.setVolume(effectVolume); // THÊM: set volume
        clip.setCycleCount(AudioClip.INDEFINITE);
        clip.play();
    }

    public void stopSound(String name) {
        AudioClip clip = AssetManager.getInstance().getSound(name);
        if (clip != null) {
            clip.stop();
            clip.setCycleCount(1);
        } else {
            System.err.println("Couldn't find sfx to stop with name: " + name);
        }
    }

    // THÊM METHOD MỚI (OPTIONAL - hữu ích khi cleanup)
    public void stopAllSounds() {
        for (AudioClip clip : AssetManager.getInstance().getSounds().values()) {
            clip.stop();
            clip.setCycleCount(1);
        }
    }

    // ============ GETTERS (OPTIONAL - để debug) ============

    public double getCurrentMusicTime() {
        if (currentMusicPlayer != null) {
            return currentMusicPlayer.getCurrentTime().toMillis();
        }
        return 0;
    }


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
        List<String> musicKey = new ArrayList<>(AssetManager.getInstance().getMusics().keySet());
        if (currentTrackIndex >= 0 && currentTrackIndex < musicKey.size()) {
            return musicKey.get(currentTrackIndex);
        }
        return null;
    }
}