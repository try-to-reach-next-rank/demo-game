package com.example.demo.managers;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.*;
import java.net.URL;

public class SoundManager {
    // tạo ra một đối tượng để quản lý + gọi
    private static final SoundManager instance = new SoundManager();

    private final Map<String, Media> musicLibrary = new HashMap<>(); // libary of songs
    private final List<String> musicKey = new ArrayList<>(); //this key was made for easier next/random song handling
    private int currentTrackIndex = 0; // keep track of next songs (in playlist)
    private MediaPlayer currentMusicPlayer; // currentl active song
    //_____________________________
    private final Map<String, AudioClip> soundEffects = new HashMap<>();

    private SoundManager() {
        loadSounds();
    }

    // tải tất cả âm thanh
    private void loadSounds(){
        try{
            loadMusic("Hametsu-no-Ringo", "/sounds/Hametsu-no-Ringo.mp3");
            loadMusic("An-Impromptu-Piece", "/sounds/An-Impromptu-Piece.mp3");
            loadMusic("Gerty-on-a-Rainy-Day", "/sounds/Gerty-on-a-Rainy-Day.mp3");
            loadMusic("You-Far-Away", "/sounds/You-Far-Away.mp3");
            loadMusic("Engraved-Star", "/sounds/Engraved-Star.mp3");

            loadSoundEffect("brick_hit", "/sounds/brick_hit.wav");
            loadSoundEffect("paddle_hit", "/sounds/paddle_hit.wav");
            loadSoundEffect("wall_hit", "/sounds/wall_hit.wav");
            loadSoundEffect("game_over", "/sounds/game_over.wav");
            loadSoundEffect("power_up","/sounds/power_up.wav");

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

    private void loadMusic(String name, String path){
        URL musicUrl = getClass().getResource(path);
        if (musicUrl == null) System.err.println("Couldn't find music file at path: " + path);
        else {
            Media media = new Media(musicUrl.toString());
            musicLibrary.put(name,media);
            musicKey.add(name);
        }
    }

    // trả duy nhất manager instance
    public static SoundManager getInstance(){
        return instance;
    }

    //public control init
    public void playMusic(String name){
        stopMusic(); //stop any music that's currently playing

        Media media = musicLibrary.get(name);
        if (media == null) {
            System.err.println("Couldn't find music with name: " + name);
            return;
        }

        currentMusicPlayer = new MediaPlayer(media);
        currentMusicPlayer.setVolume(0.5); //set background music's volume

        currentMusicPlayer.setOnEndOfMedia(() -> playNextMusic()); //After the current sond ends, instantly play next music;


        currentMusicPlayer.play();

        currentTrackIndex = musicKey.indexOf(name);
    }

    public void stopMusic(){
        if(currentMusicPlayer != null) currentMusicPlayer.stop();
    }

    public void setMusicVolume(double volume){
        if(currentMusicPlayer != null) {
            currentMusicPlayer.setVolume(volume); // range: 0.1 -> 1.0
        }
    }

    public void playNextMusic(){ // play next song in a playlist
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

    public void playSound(String name){
        AudioClip clip = soundEffects.get(name);
        if (clip == null) System.err.println("Couldn't find sfx according to its name: " + name);
        else clip.play();
    }

}
