package com.example.demo.Controller.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public abstract class UIComponent {
    protected boolean active = false;

    public void show() {active = true;}
    public void hide() {active = false;}
    public boolean isActive() {return active;}

    public abstract void update(double deltaTime);
    public abstract void render(GraphicsContext gc, double width, double height);
    public abstract void handleInput(KeyCode code);
}
