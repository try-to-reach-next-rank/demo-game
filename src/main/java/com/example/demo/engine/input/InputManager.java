package com.example.demo.engine.input;

import javafx.scene.input.KeyCode;

public class InputManager {
    private InputHandler currentHandler;

    public void setCurrentHandler(InputHandler handler) {
        this.currentHandler = handler;
    }

    public void handle(KeyCode code) {
        if (currentHandler != null) {
            currentHandler.handleInput(code);
        }
    }
}
