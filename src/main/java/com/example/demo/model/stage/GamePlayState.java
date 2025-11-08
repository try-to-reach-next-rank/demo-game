package com.example.demo.model.stage;

import com.example.demo.Main;
import com.example.demo.controller.core.GameController;
import com.example.demo.engine.state.GameState;

import com.example.demo.utils.var.GlobalVar;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



public class GamePlayState implements GameState {

    private final Main mainApp;
    private final Stage stage;
    private final StackPane root;
    private Scene scene;
    private GameController gameController;

    private final int slotNumber;

    private final com.example.demo.model.state.GameState loadedGameState;

    public GamePlayState(Main mainApp, Stage stage, int slotNumber, com.example.demo.model.state.GameState loadedGameState) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.root = new StackPane();
        this.slotNumber = slotNumber;
        this.loadedGameState = loadedGameState;
    }

    @Override
    public void enter() {
        // MVC
        gameController = new GameController();
        gameController.setCurrentSlot(slotNumber);

        if (loadedGameState == null) {
            // NEW GAME
            gameController.setNewGame(true);
            gameController.initGame();
            gameController.startIntroDialogue();
        } else {
            // LOAD GAME
            gameController.setNewGame(false);
            gameController.initGame();

            gameController.applyState(loadedGameState);
        }

        // call back về Menu
        gameController.setOnBackToMenu(() -> {
            mainApp.getStateManager().changeState(new MenuState(mainApp, stage));
        });

        // gắn scene
        root.getChildren().setAll(gameController);
        scene = new Scene(root, GlobalVar.WIDTH, GlobalVar.HEIGHT);

        gameController.requestFocus();
    }

    @Override
    public void exit() {

        if (gameController != null) {
            gameController.setOnBackToMenu(null);
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}