package com.example.demo.model.utils;

import com.example.demo.controller.core.GameManager;
import com.example.demo.controller.view.ButtonManager;
import com.example.demo.view.ui.UIComponent;
import com.example.demo.controller.view.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class PauseTable extends UIComponent {
    private final ButtonManager buttonManager;
    private final VBox container;
    private final ThemeManager themeManager;
    private final StackPane wrapper;

    public PauseTable(GameManager gameManager) {

        this.themeManager = new ThemeManager();
        this.buttonManager = new ButtonManager(themeManager.getHandImage());


        container = new VBox(16);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 30; -fx-background-radius: 15;");


        container.getChildren().add(buttonManager.createButtonRow("Resume", e -> {
            hide();
            gameManager.resumeGame();
            System.out.println("Resume game");
        }));

        container.getChildren().add(buttonManager.createButtonRow("Back Without Save", e -> {
            System.out.println("Back Without Save");
            gameManager.backToMenu();
        }));

        container.getChildren().add(buttonManager.createButtonRow("Save And Quit", e -> {
            System.out.println("Save And Quit");
            gameManager.saveGame();
            gameManager.backToMenu();
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
        buttonManager.setSelectedIndex(0);
    }

    @Override
    public void hide() {
        this.active = false;
       wrapper.setVisible(false);
        buttonManager.setSelectedIndex(-1); // clear selection
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
            case UP -> buttonManager.navigateUp();
            case DOWN -> buttonManager.navigateDown();
            case ENTER -> buttonManager.activateSelected();
            case ESCAPE, F1 -> hide();
            default -> {}
        }
    }
}
