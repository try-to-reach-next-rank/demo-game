package com.example.demo.controller.system;

import com.example.demo.engine.Renderable;
import com.example.demo.engine.Updatable;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.map.ParallaxLayer;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class ParallaxSystem implements Updatable, Renderable {
    private final List<ParallaxLayer> layers = new ArrayList<>();
    private double cameraTargetX = 0.0;
    private double cameraCurrentX = 0.0;
    private double lastCameraX = 0.0;
    private double driftSpeed = 0.0;

    private final double baseOffsetFactor;
    private final double smoothing;
    private final double[] depthRatios;
    private final GameWorld world;

    public ParallaxSystem(GameWorld world, double baseOffsetFactor, double smoothing, double[] depthRatios) {
        this.world = world;
        this.baseOffsetFactor = baseOffsetFactor;
        this.smoothing = smoothing;
        this.depthRatios = depthRatios;
    }

    public void addLayer(ParallaxLayer layer) {
        layers.add(layer);
    }

    @Override
    public void update(double deltaTime) {
        if (world == null || world.getPaddle() == null || layers.isEmpty()) return;

        // === Normalize paddle X ===
        double paddleMinX = GameVar.WIDTH_OF_WALLS;
        double paddleMaxX = GlobalVar.WIDTH - GameVar.WIDTH_OF_WALLS - world.getPaddle().getWidth();
        double normalizedPaddleX = (world.getPaddle().getX() - paddleMinX) / Math.max(GameVar.PARALLAX_NORMALIZE_DENOM_MIN, paddleMaxX - paddleMinX);
        cameraTargetX = Math.max(GameVar.PARALLAX_CAMERA_TARGET_MIN, Math.min(GameVar.PARALLAX_CAMERA_TARGET_MAX, normalizedPaddleX));

        // === Smooth camera motion ===
        double alpha = 1.0 - Math.exp(-smoothing * deltaTime);
        cameraCurrentX += (cameraTargetX - cameraCurrentX) * alpha;

        // === Skip if not moved ===
        if (Math.abs(cameraCurrentX - lastCameraX) < GameVar.PARALLAX_CAMERA_DELTA_X) return;
        lastCameraX = cameraCurrentX;

        // === Update parallax offsets ===
        double baseOffset = GlobalVar.WIDTH * baseOffsetFactor;
        for (int i = 0; i < layers.size(); i++) {
            ParallaxLayer layer = layers.get(i);
            double ratio = depthRatios[Math.min(i, depthRatios.length - 1)];

            double offset = -cameraCurrentX * baseOffset * ratio;
            if (driftSpeed != 0.0)
                offset -= driftSpeed * ratio * deltaTime;

            double wrapWidth = layer.getWrapWidth();
            if (wrapWidth > 0) {
                if (offset < -wrapWidth) offset += wrapWidth;
                else if (offset > wrapWidth) offset -= wrapWidth;
            }

            layer.setXOffset(offset);
        }

        // Also update animation frames if needed
        for (ParallaxLayer layer : layers)
            layer.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        for (ParallaxLayer layer : layers)
            layer.render(gc);
    }

    @Override
    public void clear() {
        // TODO: CLEAR
    }
}
