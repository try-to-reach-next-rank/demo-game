package com.example.demo.controller;

import com.example.demo.engine.Updatable;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.utils.GameVar;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.view.graphics.ParallaxLayer;

import java.util.List;

public class CameraManager implements Updatable {

    private double cameraTargetX = 0.0;
    private double cameraCurrentX = 0.0;
    private double lastCameraX = 0.0;
    private final double baseOffsetFactor;
    private final double smoothing;
    private final double[] depthRatios;
    private double driftSpeed = 0.0;

    private final GameWorld world;
    private final List<ParallaxLayer> layers;
    private boolean moved = false;

    public CameraManager(GameWorld world, List<ParallaxLayer> layers,
                         double baseOffsetFactor, double smoothing, double[] depthRatios) {
        this.world = world;
        this.layers = layers;
        this.baseOffsetFactor = baseOffsetFactor;
        this.smoothing = smoothing;
        this.depthRatios = depthRatios;
    }

    public void setDriftSpeed(double driftSpeed) {
        this.driftSpeed = driftSpeed;
    }

    @Override
    public void update(double deltaTime) {
        if (world.getPaddle() == null || layers == null || layers.isEmpty())
            return;

        // === Normalize paddle position ===
        double paddleMinX = GameVar.WIDTH_OF_WALLS;
        double paddleMaxX = GlobalVar.WIDTH - GameVar.WIDTH_OF_WALLS - world.getPaddle().getWidth();
        double normalizedPaddleX = (world.getPaddle().getX() - paddleMinX) / Math.max(1.0, paddleMaxX - paddleMinX);
        cameraTargetX = clamp(normalizedPaddleX);

        // === Smooth camera movement (frame independent) ===
        double alpha = 1.0 - Math.exp(-smoothing * deltaTime);
        cameraCurrentX += (cameraTargetX - cameraCurrentX) * alpha;

        // === Detect movement ===
        moved = Math.abs(cameraCurrentX - lastCameraX) > 0.0005;
        if (!moved) return; // skip unnecessary updates if camera barely moved

        lastCameraX = cameraCurrentX;

        // === Update parallax offsets only when camera moves ===
        double baseOffset = GlobalVar.WIDTH * baseOffsetFactor;
        for (int i = 0; i < layers.size(); i++) {
            ParallaxLayer layer = layers.get(i);
            double ratio = depthRatios[Math.min(i, depthRatios.length - 1)];

            double offset = -cameraCurrentX * baseOffset * ratio;
            if (driftSpeed != 0.0)
                offset -= driftSpeed * ratio * deltaTime;

            // avoid expensive modulo calls: only normalize occasionally
            double wrapWidth = layer.getWrapWidth();
            if (wrapWidth > 0) {
                if (offset < -wrapWidth) offset += wrapWidth;
                else if (offset > wrapWidth) offset -= wrapWidth;
            }

            layer.setXOffset(offset);
        }
    }

    private double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
