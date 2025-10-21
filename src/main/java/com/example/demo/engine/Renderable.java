package com.example.demo.engine;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable extends GameComponent {
    void render(GraphicsContext gc);
}
