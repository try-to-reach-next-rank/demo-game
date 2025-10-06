package com.example.demo.controller.core;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParallaxLayer extends GameObject {
    private final double            scrollRatio;
    private double                  wrapWidth;

    // THUỘC TÍNH ANIMATION
    protected List<Image>           animationFrames = null;
    protected int                   currentFrameIndex = 0;
    protected double                timeSinceLastFrame = 0.0;
    private final double            FRAME_DURATION = 0.2; // 5 FPS - Tốc độ chuyển khung hình

    // Constructor 1: Cho Layer TĨNH (Chỉ có 1 ảnh)

    /**
     *
     * width is default to original image width
     */
    public ParallaxLayer(String imagePath, double ratio) {
        super(imagePath, 0, 0);
        this.scrollRatio = ratio;
        this.wrapWidth = this.width;
        setupDimensions();
    }

    // Constructor 2: Cho Layer ĐỘNG (Nhiều ảnh)
    public ParallaxLayer(String[] imagePaths, double ratio) {
        super(imagePaths[0], 0, 0);
        this.scrollRatio = ratio;
        this.animationFrames = new ArrayList<>();
        this.animationFrames.add(this.image);
        for (int i = 1; i < imagePaths.length; i++) {
            this.animationFrames.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePaths[i]))));
        }
        this.wrapWidth = this.width;
        setupDimensions();
    }

    // Khởi tạo kích thước chung
    private void setupDimensions() {
        this.height = VARIABLES.HEIGHT;
        this.imageView.setFitHeight(this.height);
        this.imageView.setFitWidth(this.wrapWidth);
        this.width = this.wrapWidth;
    }

    public void update(double deltaTime) {
        if(animationFrames == null || animationFrames.size() <= 1) return;
        timeSinceLastFrame += deltaTime;

        if (this.timeSinceLastFrame >= FRAME_DURATION) {
            this.currentFrameIndex = (currentFrameIndex + 1) % animationFrames.size();
            this.image = animationFrames.get(currentFrameIndex);
            this.timeSinceLastFrame -= FRAME_DURATION;
        }
    }

    public void setXOffset(double offset) {
        this.x = offset;
        setPosition(this.x, this.y);
    }

    public double getXOffset() {
        return x;
    }

    public void setWrapWidth(double wrapWidth) {
        this.wrapWidth = wrapWidth;
        this.imageView.setFitWidth(this.wrapWidth);
        this.width = this.wrapWidth;
    }

    public double getWrapWidth() {
        return wrapWidth;
    }
}