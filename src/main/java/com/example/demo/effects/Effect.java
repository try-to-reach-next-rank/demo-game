package com.example.demo.effects;

import javafx.scene.canvas.GraphicsContext;

public interface Effect {
    void draw(GraphicsContext gc);
    void update();
    double duration();

    boolean isActive();
    void activate();
    void deactivate();
}
