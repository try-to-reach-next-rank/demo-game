package com.example.demo.effects;

public interface Effect {
    void draw(javafx.scene.canvas.GraphicsContext gc);
    void update();
    double duration();

    boolean isActive();
    void activate();
    void deactivate();
}
