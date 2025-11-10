package com.example.demo.engine.state;

import javafx.scene.Scene;

public interface GameState {
    void enter();
    void exit();
    Scene getScene();
}
