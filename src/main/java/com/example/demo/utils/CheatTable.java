package com.example.demo.utils;

import com.example.demo.controller.core.GameController;
import com.example.demo.model.core.effects.GlowTextEffect;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.UtilVar;
import com.example.demo.view.ui.UIComponent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class CheatTable extends UIComponent {
    private final GameController gameController;
    private final GlowTextEffect title;
    private final StackPane wrapper;

    private final String[] options = {
            "Toggle Stop Time",
            "Toggle Accelerated",
            "Toggle Stronger",
            "Load Next Map",           // Chuyển map tiếp theo
            "Load Previous Map"        // Quay lại map trước
    };

    private int selectedIndex = 0;
    private final Font titleFont = new Font("Verdana", 24);
    private final Font optionFont = new Font("Verdana", 18);

    public CheatTable(GameController gameController) {
        this.gameController = gameController;
        Text cheatMenuText = new Text("CHEAT MENU");
        this.title = new GlowTextEffect(cheatMenuText, titleFont);

        wrapper = new StackPane();
        wrapper.setPickOnBounds(false);
        wrapper.setVisible(false);
    }

    public StackPane getView() {
        return wrapper;
    }

    @Override
    public void show() {
        active = true;
        wrapper.setVisible(true);
    }

    @Override
    public void hide() {
        active = false;
        wrapper.setVisible(false);
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc, double width, double height) {
        if (!active) {
            return;
        }

        double boxX = (width - UtilVar.CHEAT_BOX_WIDTH) / 2;
        double boxY = (height - UtilVar.CHEAT_BOX_HEIGHT) / 2;

        // Overlay
        gc.setFill(Color.rgb(0, 0, 0, UtilVar.CHEAT_BOX_OPACITY));
        gc.fillRoundRect(boxX, boxY, UtilVar.CHEAT_BOX_WIDTH, UtilVar.CHEAT_BOX_HEIGHT, UtilVar.BOX_CORNER_RADIUS, UtilVar.BOX_CORNER_RADIUS); // Bo góc
        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(boxX, boxY, UtilVar.CHEAT_BOX_WIDTH, UtilVar.CHEAT_BOX_HEIGHT, UtilVar.BOX_CORNER_RADIUS, UtilVar.BOX_CORNER_RADIUS);

        //Drawing title
        double titleX = boxX + UtilVar.TITLE_OFFSET_X;
        double titleY = boxY + UtilVar.TITLE_OFFSET_Y;
        title.activate(titleX, titleY);
        title.render(gc);

        //Buttons
        gc.setFont(optionFont);
        gc.setTextAlign(TextAlignment.LEFT);

        for (int i = 0; i < options.length; i++) {
            double optionY = boxY + UtilVar.OPTION_START_Y + (i * UtilVar.OPTION_SPACING);

            if (i == selectedIndex) {
                // Đánh dấu tùy chọn đang được chọn
                gc.setFill(Color.YELLOW);
                gc.fillText("> " + options[i], boxX + UtilVar.OPTION_OFFSET_X, optionY);
            } else {
                // Các tùy chọn khác
                gc.setFill(Color.WHITE);
                gc.fillText(options[i], boxX + UtilVar.OPTION_OFFSET_X, optionY);
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
            case BACK_QUOTE:
                // Đóng cheat table
                hide();
                break;
        }
    }

    private void executeCheat() {
        String selectedOption = options[selectedIndex];

        switch (selectedOption) {
            case "Toggle Stop Time":
                gameController.getBall().toggleStopTime();
                break;
            case "Toggle Accelerated":
                gameController.getBall().toggleAccelerated();
                break;
            case "Toggle Stronger":
                gameController.getBall().toggleStronger();
                break;
            case "Load Next Map":
                hide();
                gameController.loadNextLevel();
                break;
            case "Load Previous Map":
                hide();
                gameController.loadPreviousLevel();
                break;
        }
    }
}
