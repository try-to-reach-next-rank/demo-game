package com.example.demo.view;

import com.example.demo.model.utils.GlobalVar;
import com.example.demo.view.graphics.ParallaxLayer;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class RenderManager {
    private final double screenWidth;
    private final double screenHeight;

    public RenderManager(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void drawParallax(GraphicsContext gc, List<ParallaxLayer> layers) {
        if (layers == null || layers.isEmpty()) return;

        for (ParallaxLayer layer : layers) {
            if (layer == null) continue;

            double x = layer.getXOffset();
            double w = layer.getWrapWidth();

            if (x + w < 0 || x > GlobalVar.WIDTH)
                continue;

            layer.render(gc);
        }
    }
}
