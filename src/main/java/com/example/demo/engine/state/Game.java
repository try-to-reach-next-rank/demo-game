package com.example.demo.engine.state;

import javafx.stage.Stage;

public class Game {
    private Stage stage;
    private GameState currentState;

    public Game(Stage stage) {
        this.stage = stage;
    }

    public void setState(GameState newState) {
        if (currentState != null) {
            currentState.exit();
        }
        currentState = newState;
        currentState.enter();
        stage.setScene(currentState.getScene());
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }
}
