package com.example.demo.utils;

import com.example.demo.controller.core.GameController;
import com.example.demo.controller.view.ButtonController;
import com.example.demo.view.ui.UIComponent;
import com.example.demo.controller.view.ThemeController;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class PauseTable extends UIComponent {
    private final ButtonController buttonController;
    private final VBox container;
    private final ThemeController themeController;
    private final StackPane wrapper;

    public PauseTable(GameController gameController) {

        this.themeController = new ThemeController();
        this.buttonController = new ButtonController(themeController.getHandImage());


        container = new VBox(16);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 30; -fx-background-radius: 15;");


        container.getChildren().add(buttonController.createButtonRow("Resume", e -> {
            hide();
            gameController.resumeGame();
            System.out.println("Resume game");
        }));

        container.getChildren().add(buttonController.createButtonRow("Back Without Save", e -> {
            System.out.println("Back Without Save");
            gameController.backToMenu();
        }));

        container.getChildren().add(buttonController.createButtonRow("Save And Quit", e -> {
            System.out.println("Save And Quit");
            gameController.saveGame();
            gameController.backToMenu();
        }));


        wrapper = new StackPane(container);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setVisible(false);
        wrapper.setPickOnBounds(false);
        wrapper.getStylesheets().add(
                getClass().getResource("/styles/menu.css").toExternalForm()
        );
    }


    public StackPane getView() {
        return wrapper;
    }

    @Override
    public void show() {
        this.active = true;
        wrapper.setVisible(true);
        buttonController.setSelectedIndex(0);
    }

    @Override
    public void hide() {
        this.active = false;
       wrapper.setVisible(false);
        buttonController.setSelectedIndex(-1); // clear selection
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc, double width, double height) {
    }

    @Override
    public void handleInput(KeyCode code) {
        if (!active) return;

        switch (code) {
            case UP -> buttonController.navigateUp();
            case DOWN -> buttonController.navigateDown();
            case ENTER -> buttonController.activateSelected();
            case F1 -> hide();
            default -> {}
        }
    }
}
