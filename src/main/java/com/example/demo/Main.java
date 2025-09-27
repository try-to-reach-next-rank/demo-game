package com.example.demo;

import com.example.demo.managers.GameManager;
import com.example.demo.core.VARIABLES;
import com.example.demo.utils.InputHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GameManager gameManager = new GameManager();

        Scene scene = new Scene(gameManager, VARIABLES.WIDTH, VARIABLES.HEIGHT);

        stage.setTitle("Brick Breaker");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        InputHandler inputHandler = new InputHandler(
            gameManager.getPaddle(), gameManager.getBall()
        );
        inputHandler.input(scene);

        gameManager.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
