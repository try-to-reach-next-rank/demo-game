package com.example.demo.view;

import com.example.demo.model.utils.GlobalVar;
import com.example.demo.view.graphics.ParallaxLayer;
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
            if(x + w < GlobalVar.WIDTH) {
                gc.drawImage(img, x + w, 0, w, h);
            }
        }
    }
}
