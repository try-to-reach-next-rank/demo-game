package com.example.demo.view;

import com.example.demo.model.menu.SaveSlot;
import com.example.demo.model.state.BrickData;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class SlotComponent extends VBox {
    private final SaveSlot slot;
    private Canvas miniMapCanvas;
    private HBox buttonBox;
    private Button playButton;
    private Button deleteButton;
    private Button newGameButton;

    private static final double MAP_WIDTH = 210;
    private static final double MAP_HEIGHT = 190;
    private static final int MAP_COLS = 21;
    private static final int MAP_ROWS = 19;

    public SlotComponent(SaveSlot slot) {
        this.slot = slot;
        setupLayout();
        setupMiniMap();
        setupButtons();
    }

    private void setupLayout() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 20;" +
                        "-fx-border-color: #2a6cff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;"
        );
    }

    private void setupMiniMap() {
        miniMapCanvas = new Canvas(MAP_WIDTH, MAP_HEIGHT);

        if (slot.isEmpty()) {
            renderEmptySlot();
        } else {
            renderMiniMap();
        }

        miniMapCanvas.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"
        );

        this.getChildren().add(miniMapCanvas);
    }

    private void renderEmptySlot() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();

        gc.setFill(Color.web("#04263a"));
        gc.fillRect(0, 0, MAP_WIDTH, MAP_HEIGHT);

        gc.setStroke(Color.web("#2a6cff", 0.3));
        gc.setLineWidth(2);
        gc.strokeRect(1, 1, MAP_WIDTH - 2, MAP_HEIGHT - 2);

        gc.setFill(Color.web("#ffffff", 0.2));
        gc.setFont(Font.font(48));
        gc.fillText("+", MAP_WIDTH / 2 - 15, MAP_HEIGHT / 2 + 15);
    }

    private void renderMiniMap() {
        GraphicsContext gc = miniMapCanvas.getGraphicsContext2D();
        List<BrickData> bricksData = slot.getBricksData();

        if (bricksData == null) {
            renderEmptySlot();
            return;
        }

        gc.setFill(Color.web("#04263a"));
        gc.fillRect(0, 0, MAP_WIDTH, MAP_HEIGHT);

        double brickW = MAP_WIDTH / MAP_COLS;
        double brickH = MAP_HEIGHT / MAP_ROWS;

        for (BrickData brick : bricksData) {
            if (brick.isDestroyed()) continue;

            int row = brick.getId() / MAP_COLS;
            int col = brick.getId() % MAP_COLS;

            double x = col * brickW;
            double y = row * brickH;

            gc.setFill(getBrickColor(brick.getHealth()));
            gc.fillRect(x, y, brickW - 1, brickH - 1);
        }

        gc.setStroke(Color.web("#2a6cff", 0.5));
        gc.setLineWidth(2);
        gc.strokeRect(1, 1, MAP_WIDTH - 2, MAP_HEIGHT - 2);
    }

    private Color getBrickColor(int health) {
        return switch(health) {
            case 1 -> Color.web("#4a9eff");
            case 2 -> Color.web("#ffd700");
            case 3 -> Color.web("#ff8c00");
            default -> Color.web("#ff4444");
        };
    }

    private void setupButtons() {
        buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        if (slot.isEmpty()) {
            newGameButton = createButton("New Game", 200);
            buttonBox.getChildren().add(newGameButton);
        } else {
            playButton = createButton("Play", 95);
            deleteButton = createButton("Delete", 95);
            buttonBox.getChildren().addAll(playButton, deleteButton);
        }

        this.getChildren().add(buttonBox);
    }

    private Button createButton(String text, double width) {
        Button button = new Button(text);
        button.setMinWidth(width);
        button.setFont(Font.font(14));
        button.getStyleClass().add("menu-button");
        button.setFocusTraversable(false);
        return button;
    }

    // Getters
    public Button getPlayButton() {
        return playButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }

    public SaveSlot getSlot() {
        return slot;
    }

    public Canvas getMiniMapCanvas() {
        return miniMapCanvas;
    }
}