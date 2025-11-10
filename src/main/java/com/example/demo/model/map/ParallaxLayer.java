package com.example.demo.model.map;

import com.example.demo.engine.Renderable;
import com.example.demo.engine.Updatable;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

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

    public ParallaxLayer(String imagePath, double ratio) {
        this.scrollRatio = ratio;
        this.frames = new ArrayList<>();
        this.frames.add(loadImage(imagePath));
        this.wrapWidth = this.frames.get(0).getWidth();
    }

    private Image loadImage(String path) {
        return new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(path)),
                GlobalVar.WIDTH,
                GlobalVar.HEIGHT,
                GameVar.PARALLAX_PRESERVE_RATIO,
                GameVar.PARALLAX_SMOOTH_SCALING
        );
    }

    @Override
    public void update(double deltaTime) {
        if (frames.size() <= 1) return;
        timeSinceLastFrame += deltaTime;
        if (timeSinceLastFrame >= GameVar.PARALLAX_FRAME_DURATION) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
            timeSinceLastFrame -= GameVar.PARALLAX_FRAME_DURATION;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Image frame = frames.get(currentFrameIndex);
        double imgWidth = frame.getWidth();

        // draw twice for seamless wrap
        double drawX = xOffset % imgWidth;
        if (drawX > 0) drawX -= imgWidth;

        gc.drawImage(frame, drawX, GameVar.PARALLAX_Y_OFFSET);
        gc.drawImage(frame, drawX + imgWidth, GameVar.PARALLAX_FRAME_DURATION);
    }

    public void clear() {
        // TODO: CLEAR
    }

    public void setXOffset(double offset) {
        this.xOffset = offset;
    }

    public double getWrapWidth() {
        return wrapWidth;
    }
}
