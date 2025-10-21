package com.example.demo.model.map;

import com.example.demo.engine.Renderable;
import com.example.demo.engine.Updatable;
import com.example.demo.model.utils.GlobalVar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ParallaxLayer: lightweight, GPU-stable version for Canvas rendering.
 * Does NOT use ImageView or scene graph nodes.
 */
public class ParallaxLayer implements Updatable, Renderable {
    private final double scrollRatio;
    private double xOffset = 0.0;
    private double wrapWidth;

    // Animation
    private final List<Image> frames;
    private int currentFrameIndex = 0;
    private double timeSinceLastFrame = 0.0;
    private static final double FRAME_DURATION = 0.2; // 5 FPS

    public ParallaxLayer(String imagePath, double ratio) {
        this.scrollRatio = ratio;
        this.frames = new ArrayList<>();
        this.frames.add(loadImage(imagePath));
        this.wrapWidth = this.frames.get(0).getWidth();
    }

    public ParallaxLayer(String[] imagePaths, double ratio) {
        this.scrollRatio = ratio;
        this.frames = new ArrayList<>();
        for (String path : imagePaths) {
            this.frames.add(loadImage(path));
        }
        this.wrapWidth = this.frames.get(0).getWidth();
    }

    private Image loadImage(String path) {
        return new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(path)),
                GlobalVar.WIDTH, GlobalVar.HEIGHT, false, true
        );
    }

    @Override
    public void update(double deltaTime) {
        if (frames.size() <= 1) return;
        timeSinceLastFrame += deltaTime;
        if (timeSinceLastFrame >= FRAME_DURATION) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
            timeSinceLastFrame -= FRAME_DURATION;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Image frame = frames.get(currentFrameIndex);
        double imgWidth = frame.getWidth();

        // draw twice for seamless wrap
        double drawX = xOffset % imgWidth;
        if (drawX > 0) drawX -= imgWidth;

        gc.drawImage(frame, drawX, 0);
        gc.drawImage(frame, drawX + imgWidth, 0);
    }

    public void setXOffset(double offset) {
        this.xOffset = offset;
    }

    public void setWrapWidth(double wrapWidth) {
        this.wrapWidth = wrapWidth;
    }

    public double getWrapWidth() {
        return wrapWidth;
    }

    public double getScrollRatio() {
        return scrollRatio;
    }
}
