package com.example.demo.Model;

import com.example.demo.Controller.core.ParallaxLayer;
import com.example.demo.Controller.core.VARIABLES;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class RenderManager {
    private final double screenWidth;
    private final double screenHeight;

    public RenderManager(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void drawParallax(GraphicsContext gc, List<ParallaxLayer> layers) {
        for(ParallaxLayer layer : layers) {
            Image img = layer.getImage();
            double x = layer.getX();
            double w = layer.getWidth();
            double h = layer.getHeight();

            gc.drawImage(img, x, 0, w, h);
            if(x + w < VARIABLES.WIDTH) {
                gc.drawImage(img, x + w, 0, w, h);
            }
        }
    }
}
