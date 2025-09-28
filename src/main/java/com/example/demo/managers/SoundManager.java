package com.example.demo.managers;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    // tạo ra một đối tượng để quản lý + gọi
    private static final SoundManager instance = new SoundManager();

    private MediaPlayer backgroundMusic;
    private final Map<String, AudioClip> soundEffects = new HashMap<>();

    private SoundManager() {
        loadSounds();
    }

    // tải tất cả âm thanh
    private void loadSounds(){
        try{
            URL musicUrl = getClass().getResource("/sounds/Soft-Memories.mp3");

            if (musicUrl == null) System.err.println("Couldn't find background music");
            else {
                Media backgroundMedia = new Media(musicUrl.toString());
                backgroundMusic =  new MediaPlayer(backgroundMedia);

                //lặp lại
                backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            }

            loadSoundEffect("brick_hit", "/sounds/brick_hit.mp3");
            loadSoundEffect("paddle_hit", "/sounds/paddle_hit.mp3");
            loadSoundEffect("wall_hit", "/sounds/wall_hit.mp3");
            loadSoundEffect("game_over", "/sounds/game_over.mp3");
            loadSoundEffect("power_up","/sounds/power_up.mp3");

        }
        catch (Exception e){
            System.err.println("Error when loading sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSoundEffect(String name, String path){
        URL sfxUrl = getClass().getResource(path);
        if (sfxUrl == null) System.err.println("Couldn't find SFX at path: " + path);
        else{
            AudioClip clip = new AudioClip(sfxUrl.toString());
            soundEffects.put(name,clip);
        }
    }

    // trả duy nhất manager instance
    public static SoundManager getInstance(){
        return instance;
    }

    //public control init
    public void playBackgroundMusic(){
        if(backgroundMusic != null)
            backgroundMusic.play();
    }
    public void stopBackgroundMusic(){
        if(backgroundMusic != null)
            backgroundMusic.stop();
    }
    public void setBackgroundMusicVolume(double volume){
        if(backgroundMusic != null)
            //volume range: 0.0 -> 1.0
            backgroundMusic.setVolume(volume);
    }

    public void playSound(String name){
        AudioClip clip = soundEffects.get(name);
        if (clip == null) System.err.println("Couldn't find sfx according to its name: " + name);
        else clip.play();
    }

}
