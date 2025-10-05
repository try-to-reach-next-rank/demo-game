package com.example.demo.managers;

import com.example.demo.core.ParallaxLayer;

import java.util.List;

public class CameraManager {
    private double              cameraTargetX = 0.0;
    private double              cameraCurrentX = 0.0;
    private final double        baseOffsetFactor;
    private final double        smoothing;
    private final double[]      depthRatios;
    private double              driftSpeed = 0.0;

    public CameraManager(double baseOffsetFactor, double smoothing, double[] depthRatios) {
        this.baseOffsetFactor = baseOffsetFactor;
        this.smoothing = smoothing;
        this.depthRatios = depthRatios;
    }

    public void setDriftSpeed(double driftSpeed) { this.driftSpeed = driftSpeed; }

    /**
     * Update camera current value (call every frame) and compute X offsets for each layer.
     * @param normalizedPaddleX value in [0,1]
     * @param screenWidth current screen width
     * @param layers list of parallax layers to update
     * @param deltaTime seconds since last update
     */
    public void update(double normalizedPaddleX, double screenWidth, List<ParallaxLayer> layers, double deltaTime) {
        cameraTargetX = clamp(normalizedPaddleX);

        // frame-rate independent smoothing factor
        double alpha = 1 - Math.exp(-smoothing * deltaTime);
        cameraCurrentX += (cameraTargetX - cameraCurrentX) * alpha;

        double baseOffset = screenWidth * baseOffsetFactor;

        for (int i = 0; i < layers.size(); i++) {
            ParallaxLayer layer = layers.get(i);
            double ratio = depthRatios[Math.min(i, depthRatios.length - 1)];
            double offset = -cameraCurrentX * baseOffset * ratio;

            // apply tiny cinematic drift
            if (driftSpeed != 0.0) {
                offset -= driftSpeed * ratio * deltaTime;
            }

            layer.setXOffset(offset % layer.getWrapWidth());
        }
    }

    private double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    public double getCameraCurrentX() {
        return cameraCurrentX;
    }
}
