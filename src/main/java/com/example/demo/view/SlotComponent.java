package com.example.demo.view;

import com.example.demo.controller.view.ButtonController;
import com.example.demo.model.menu.SaveSlot;
import com.example.demo.model.state.BrickData;
import com.example.demo.view.ui.UIComponent;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

/**
 * SlotComponent - Toàn bộ slot là 1 button lớn
 * Bên trong chứa mini map và action buttons
 */
public class SlotComponent extends Button{
    private final SaveSlot slot;
    private final Canvas miniMapCanvas;
    private final VBox content;
    private final HBox buttonBox;

    private static final double MAP_WIDTH = 210;
    private static final double MAP_HEIGHT = 190;
    private static final int MAP_COLS = 21;
    private static final int MAP_ROWS = 19;

    public SlotComponent(SaveSlot slot) {
        this.slot = slot;

        // Setup mini map
        this.miniMapCanvas = new Canvas(MAP_WIDTH, MAP_HEIGHT);
        setupMiniMap();

        // Setup button container
        this.buttonBox = new HBox(10);
        this.buttonBox.setAlignment(Pos.CENTER);

        // Content layout
        this.content = new VBox(15);
        this.content.setAlignment(Pos.CENTER);
        this.content.getChildren().addAll(miniMapCanvas, buttonBox);

        setupSlotButton();
    }

    private void setupSlotButton() {
        this.setGraphic(content);
        this.setText(null);
        this.setFocusTraversable(false);

        setSelected(false);

    }

    private void setupMiniMap() {
        if (slot.isEmpty()) {
            renderEmptySlot();
        } else {
            renderMiniMap();
        }

        miniMapCanvas.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"
        );
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

    public void setSelected(boolean selected) {
        if (selected) {
            if (!this.getStyleClass().contains("selected")) {
                this.getStyleClass().add("selected");
            }
            this.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 20;" +
                            "-fx-border-color: #00ff88;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 10;" +
                            "-fx-effect: dropshadow(gaussian, #00ff88, 15, 0.5, 0, 0);"
            );
        } else {
            this.getStyleClass().remove("selected");
            this.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 20;" +
                            "-fx-border-color: #2a6cff;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;"
            );
        }
    }

    // Getters
    public HBox getButtonBox() {
        return buttonBox;
    }

    public SaveSlot getSlot() {
        return slot;
    }
}
