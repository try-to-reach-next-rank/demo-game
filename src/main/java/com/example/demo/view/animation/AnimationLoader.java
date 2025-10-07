package com.example.demo.view.animation;

import java.util.Objects;
import com.example.demo.model.utils.GlobalVar;

import javafx.scene.image.Image;

public class AnimationLoader {
    private String  resourcePath;
    private int     frameCount;
    private double  frameDuration = 1.0 / GlobalVar.FPS;
    private boolean loop          = false;

    // Constructor
    public AnimationLoader(String resourcePath, int frameCount) {
        this.resourcePath = resourcePath;
        this.frameCount   = frameCount;
    }

    // Set frame duration
    public AnimationLoader setFrameDuration(double frameDuration) {
        this.frameDuration = frameDuration;
        return this;
    }

    // Set loop
    public AnimationLoader setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    // Load the animation
    public Animation load() {
        Image[] frames = new Image[frameCount];
        
        for (int i = 0; i < frameCount; i++) {
            String imagePath = String.format("%s/frame_%d.png", resourcePath, i);
        
            try {
                frames[i] = new Image(
                    Objects.requireNonNull(
                        getClass().getResourceAsStream(imagePath)
                    )
                );

                if (frames[i].isError()) {
                    System.err.println("Failed to load frame: " + imagePath);
                }
            } catch (Exception e) {
                System.err.println("Exception loading frame: " + imagePath);
                e.printStackTrace();
            }        
        }

        return new Animation(frames, frameDuration, loop);
    }
}
