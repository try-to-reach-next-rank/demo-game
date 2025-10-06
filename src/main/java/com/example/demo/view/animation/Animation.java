package com.example.demo.view.animation;

import javafx.scene.image.Image;

public class Animation {
    private Image[] frames;
    private double  frameDuration;        // Duration of each frame in seconds
    private double  elapsedTime  = 0.0;   // Time since the last frame change
    private boolean playing      = true;
    private int     currentFrame = 0;
    private boolean loop;

    // Constructor
    public Animation(Image[] frames, double frameDuration) {
        this.frames        = frames;
        this.frameDuration = frameDuration;
        this.loop          = false;
    }

    // Constructor with loop option
    public Animation(Image[] frames, double frameDuration, boolean loop) {
        this.frames        = frames;
        this.frameDuration = frameDuration;
        this.loop          = loop;
    }

    // Update the animation based on the elapsed time
    public void update(double deltaTime) {
        if (!playing || frames.length == 0) return;

        this.elapsedTime += deltaTime;
        if (this.elapsedTime >= this.frameDuration) {
            this.elapsedTime = 0.0;
            this.currentFrame++;

            if (this.currentFrame >= this.frames.length) {
                if (loop) {
                    this.currentFrame = 0;
                } else {
                    this.currentFrame = this.frames.length - 1;  // Stay on the last frame
                    this.playing      = false;                   // Stop the animation
                }
            }
        }
    }

    // Get frames array (used for cloning)
    public Image[] getFrames() {
        return frames;
    }

    // Get the current frame to be drawn
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }

    // Getter / Setter for loop
    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
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
