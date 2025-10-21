package com.example.demo.engine;

import javafx.scene.canvas.GraphicsContext;

public interface Effect {
    void update(double deltaTime);
    void render(GraphicsContext gc);
    
    boolean isActive();

    void activate(double x, double y, double durationSeconds);
    void deactivate();
}
