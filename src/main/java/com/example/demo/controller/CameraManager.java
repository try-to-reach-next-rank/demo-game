package com.example.demo.controller;

import com.example.demo.engine.Updatable;
import com.example.demo.engine.GameWorld;
import com.example.demo.view.graphics.ParallaxLayer;

import java.util.List;

public class CameraManager implements Updatable {
    private double cameraTargetX = 0.0;
    private double cameraCurrentX = 0.0;
    private final double baseOffsetFactor;
    private final double smoothing;
    private final double[] depthRatios;
    private double driftSpeed = 0.0;

    private final GameWorld world;
    private final List<ParallaxLayer> layers;

    public CameraManager(GameWorld world, List<ParallaxLayer> layers,
                         double baseOffsetFactor, double smoothing, double[] depthRatios) {
        this.world = world;
        this.layers = layers;
        this.baseOffsetFactor = baseOffsetFactor;
        this.smoothing = smoothing;
        this.depthRatios = depthRatios;
    }

    public void setDriftSpeed(double driftSpeed) { this.driftSpeed = driftSpeed; }

    @Override
    public void update(double deltaTime) {
        if (world.getPaddle() == null || layers == null || layers.isEmpty()) return;

        // compute normalized paddle X in [0,1]
        double paddleMinX = com.example.demo.model.utils.GameVar.WIDTH_OF_WALLS;
        double paddleMaxX = com.example.demo.model.utils.GlobalVar.WIDTH - com.example.demo.model.utils.GameVar.WIDTH_OF_WALLS - world.getPaddle().getWidth();
        double paddleRange = Math.max(1.0, paddleMaxX - paddleMinX);

        double normalizedPaddleX = (world.getPaddle().getX() - paddleMinX) / paddleRange;
        normalizedPaddleX = Math.max(0.0, Math.min(1.0, normalizedPaddleX));

        cameraTargetX = clamp(normalizedPaddleX);

        // frame-rate independent smoothing factor
        double alpha = 1 - Math.exp(-smoothing * deltaTime);
        cameraCurrentX += (cameraTargetX - cameraCurrentX) * alpha;

        double baseOffset = com.example.demo.model.utils.GlobalVar.WIDTH * baseOffsetFactor;

        for (int i = 0; i < layers.size(); i++) {
            ParallaxLayer layer = layers.get(i);
            double ratio = depthRatios[Math.min(i, depthRatios.length - 1)];
            double offset = -cameraCurrentX * baseOffset * ratio;

            if (driftSpeed != 0.0) {
                offset -= driftSpeed * ratio * deltaTime;
            }

            layer.setXOffset(offset % layer.getWrapWidth());
        }
    }

    private double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    public double getCameraCurrentX() { return cameraCurrentX; }
}
