package com.example.demo.utils;

import javafx.scene.image.Image;

public class Animation {
    private Image[] frames;
    private double frameDuration;       // Duration of each frame in seconds
    private double elapsedTime = 0.0;   // Time since the last frame change
    private boolean playing    = true;
    private int currentFrame   = 0;

    // Constructor
    public Animation(Image[] frames, double frameDuration) {
        this.frames        = frames;
        this.frameDuration = frameDuration;
    }

    // Update the animation based on the elapsed time
    public void update(double deltaTime) {
        if (!playing || frames.length == 0) return;

        elapsedTime += deltaTime;
        if (elapsedTime >= frameDuration) {
            currentFrame = (currentFrame + 1) % frames.length;  // Loop back to first frame
            elapsedTime  = 0.0;
        }
    }

    // Get the current frame to be drawn
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }

    // Getter / Setter for frame duration
    public double getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(double frameDuration) {
        this.frameDuration = frameDuration;
    }

    // Start the animation
    public void start() {
        playing = true;
    }

    // Stop the animation
    public void stop() {
        playing = false;
    }

    // Check if the animation is playing
    public boolean isPlaying() {
        return playing;
    }

    // Reset the animation to the first frame
    public void reset() {
        currentFrame = 0;
        elapsedTime  = 0.0;
    }
}
