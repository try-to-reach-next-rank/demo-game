package com.example.demo.view.effects;

public interface Effect {
    void draw(javafx.scene.canvas.GraphicsContext gc);
    void update(double deltaTime);
    double duration();

    boolean isActive();
    void activate();
    void deactivate();
}
