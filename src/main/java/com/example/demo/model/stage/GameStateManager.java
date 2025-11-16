package com.example.demo.model.stage;

import com.example.demo.Main;
import com.example.demo.engine.state.Game;
import com.example.demo.engine.state.GameState;
import com.example.demo.model.stage.MenuState; // Cần import các State cụ thể nếu Manager phụ trách khởi tạo
import javafx.stage.Stage;

public class GameStateManager {
    private final Game gameContext;
    private final Stage stage;
    private final Main mainApp;

    public GameStateManager(Main mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.gameContext = new Game(stage);
    }

    public void changeState(GameState newState) {
        gameContext.setState(newState);
    }

    public Stage getStage() {
        return stage;
    }
}