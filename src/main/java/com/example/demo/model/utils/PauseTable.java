package com.example.demo.model.utils;

import com.example.demo.controller.core.GameManager;
import com.example.demo.model.core.effects.GlowTextEffect;
import com.example.demo.view.ui.UIComponent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PauseTable extends UIComponent {
    private final GameManager gameManager;
    private final GlowTextEffect title;
    // Danh sách các tùy chọn
    private final String[] options = {
            "Resume",
            "Back Without Save",
            "Save And Quit",
    };

    private int selectedIndex = 0;

    // Fonts để vẽ
    private final Font titleFont = new Font("Verdana", 24);
    private final Font optionFont = new Font("Verdana", 18);

    public PauseTable(GameManager gameManager) {
        this.gameManager = gameManager;
        Text cheatMenuText = new Text("PAUSE MENU");
        this.title = new GlowTextEffect(cheatMenuText, titleFont);
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc, double width, double height) {
        if (!active) {
            return;
        }

        double boxWidth = 400;
        double boxHeight = 250;
        double boxX = (width - boxWidth) / 2;
        double boxY = (height - boxHeight) / 2;

        // Overlay
        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15); // Bo góc
        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        //Drawing title
        double titleX = boxX + 50;
        double titleY = boxY + 45;
        title.activate(titleX, titleY);
        title.render(gc);

        //Buttons
        gc.setFont(optionFont);
        gc.setTextAlign(TextAlignment.LEFT);

        for (int i = 0; i < options.length; i++) {
            double optionY = boxY + 90 + (i * 30);

            if (i == selectedIndex) {
                // Đánh dấu tùy chọn đang được chọn
                gc.setFill(Color.YELLOW);
                gc.fillText("> " + options[i], boxX + 40, optionY);
            } else {
                // Các tùy chọn khác
                gc.setFill(Color.WHITE);
                gc.fillText(options[i], boxX + 40, optionY);
            }
        }
    }

    @Override
    public void handleInput(KeyCode code) {
        if (!active) {
            return;
        }

        switch (code) {
            case UP:
                // Di chuyển lựa chọn lên
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = options.length - 1;
                }
                // (Tùy chọn) Thêm âm thanh "bíp" ở đây
                break;

            case DOWN:
                // Di chuyển lựa chọn xuống
                selectedIndex++;
                if (selectedIndex >= options.length) {
                    selectedIndex = 0;
                }
                // (Tùy chọn) Thêm âm thanh "bíp" ở đây
                break;

            case ENTER:
            case SPACE:
                // Thực thi cheat
                executeCheat();
                // (Tùy chọn) Thêm âm thanh "chọn" ở đây
                break;

            case ESCAPE:
            case F1:
                // Đóng cheat table
                hide();
                break;
        }
    }

    private void executeCheat() {
        String selectedOption = options[selectedIndex];



        switch (selectedOption) {
            case "Resume":
                hide();
            case "Back Without Save":
                gameManager.backToMenu();
            case "Save And Quit":
                gameManager.saveGame();
                gameManager.backToMenu();
        }
    }
}
