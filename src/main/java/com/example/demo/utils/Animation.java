package com.example.demo.utils;

import com.example.demo.utils.var.GlobalVar;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Animation {
    private final Image spriteSheet;
    private final double frameWidth;
    private final double frameHeight;
    private final int totalFrames;

    private boolean loop;
    private boolean playing;
	private boolean finished;
    
    // Default
    private int rows = 1;
    private int currentFrame = 0;
    private double elapsedTime = 0.0;
    private double frameDuration = 1.0 / GlobalVar.FPS;
    private double renderWidth = -1;
    private double renderHeight = -1;

    // Constructor with default loop = false
    public Animation(Image spriteSheet, double frameWidth, double frameHeight, int totalFrames) {
        this.spriteSheet = spriteSheet;
        this.frameWidth  = frameWidth;
        this.frameHeight = frameHeight;
        this.totalFrames = totalFrames;
        this.loop        = false;        // Default loop = false
    }

    public void update(double deltaTime) {
        if (!playing) {
            // Log here
            return;
        }

        this.elapsedTime += deltaTime;

        if (this.elapsedTime < this.frameDuration) 
            return;
        
        this.elapsedTime -= frameDuration;
        this.currentFrame++;

        if (this.currentFrame >= totalFrames) {
            if (this.loop) {
                currentFrame = 0;
            }
            else {
                this.currentFrame = this.totalFrames - 1;
                this.playing      = false;
                this.finished     = true;
            }
        }
    }

    public void render(GraphicsContext gc, double x, double y) {
        double w = renderWidth > 0 ? renderWidth : frameWidth;
        double h = renderHeight > 0 ? renderHeight : frameHeight;
        render(gc, x, y, w, h);
    }

    public void render(GraphicsContext gc, double x, double y, double w, double h) {
        if (finished) return;

        double sx = currentFrame * frameWidth;
        double sy = rows * frameHeight;

        // Default render at the center
        gc.drawImage(
            spriteSheet,
            sx, sy, frameWidth, frameHeight,    // Source
            x - w / 2 , y - h / 2, w, h         // Destination
        );
    }

    // --- Builder ---
    public Animation setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public Animation setRenderSize(double width, double height) {
        this.renderWidth = width;
        this.renderHeight = height;
        return this;
    }

    public Animation setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public Animation setDuration(double durationSeconds) {
        if (durationSeconds <= 0) {
            // Log here
            return this; 
        }
        
        this.frameDuration = durationSeconds / this.totalFrames;
        return this;
    }

    public Animation clone() {
        Animation copy = new Animation(
            this.spriteSheet,
            this.frameWidth,
            this.frameHeight,
            this.totalFrames
        );

        copy.rows = this.rows;
        copy.loop           = this.loop;
        copy.frameDuration  = this.frameDuration;
        copy.renderWidth = this.renderWidth;
        copy.renderHeight = this.renderHeight;

        copy.restart();

        return copy;
    }

    // --- Controls ---
    public void reset() {
        this.currentFrame = 0;
        this.elapsedTime = 0.0;
        this.finished = false;
    }

    public void play() {
        this.playing  = true;
        this.finished = false;
    }

    public void pause() {
        this.playing = false;
    }

    public void stop() {
        pause();
        reset();
    }

    public void restart() {
        reset();
        play();
    }

    // --- Getters ---
    public Image getSpriteSheet() {
        return this.spriteSheet;
    }

    public double getFrameWidth() {
        return this.frameWidth;
    }

    public double getFrameHeight() {
        return this.frameHeight;
    }

    public double getRenderWidth() {
        return this.renderWidth > 0 ? this.renderWidth : this.frameWidth;
    }

    public double getRenderHeight() {
        return this.renderHeight > 0 ? this.renderHeight : this.frameHeight;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isLoop() {
        return this.loop;
    }
}
