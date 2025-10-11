package com.example.demo;

import com.example.demo.controller.GameManager;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Input;
import javafx.application.Application;

import com.example.demo.controller.MenuControll;
import com.example.demo.model.states.MenuModel;
import com.example.demo.view.MenuView;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        GameManager gameManager = new GameManager();

        Scene scene = new Scene(gameManager, GlobalVar.WIDTH, GlobalVar.HEIGHT);

        stage.getIcons().add(new Image(getClass().getResource("/images/icon.png").toExternalForm())); //add window's icon
        stage.setTitle("Brick Breaker");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Input input = new Input(
            gameManager.getPaddle(), gameManager.getBall()
        );
        scene.setOnKeyPressed(e -> {
            if (gameManager.getUIManager().hasActiveUI()) {
                gameManager.getUIManager().handleInput(e.getCode());
            } else {
                input.handleKeyPressed(e.getCode());
            }
        });

        scene.setOnKeyReleased(e -> {
            if (!gameManager.getUIManager().hasActiveUI()) {
                input.handleKeyReleased(e.getCode());
            }
        });

        gameManager.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
