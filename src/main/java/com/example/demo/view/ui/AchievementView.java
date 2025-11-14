package com.example.demo.view.ui;

import com.example.demo.controller.core.SaveController;
import com.example.demo.controller.view.AchievementController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.ui.AbstractUIView;
import com.example.demo.engine.ui.UISelectionController;
import com.example.demo.model.menu.Achievement;
import com.example.demo.model.menu.ButtonManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

public class AchievementView extends AbstractUIView {

    private final AchievementController controller;
    private final VBox uiBox;
    private final ButtonManager buttonManager;
    private final UISelectionController selectionController;

    private final VBox contentBox;
    private final Pane buttonOverlay; // Pane riêng cho buttons

    private boolean showingPage1 = true;

    public AchievementView(AchievementController controller, ThemeController themeController) {
        super(themeController);
        this.controller = controller;
        this.buttonManager = new ButtonManager(themeController.getHandImage());

        this.uiBox = new VBox(30);
        this.uiBox.setPadding(new Insets(30));
        this.uiBox.setAlignment(Pos.CENTER);

        this.contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);

        // Tạo overlay Pane cho buttons (absolute positioning)
        this.buttonOverlay = new Pane();
        buttonOverlay.setPickOnBounds(false); // Cho phép click-through ở vùng trống

        buildUI();

        this.selectionController = new UISelectionController(List.of("Back to Menu"));
        selectionController.setOnConfirm(this::handleConfirm);

        root.getChildren().addAll(uiBox, buttonOverlay); // Add overlay sau cùng để nằm trên cùng
        StackPane.setAlignment(uiBox, Pos.CENTER);

    }

    @Override
    public void buildUI() {
        Text title = new Text("Achievements");
        title.getStyleClass().add("title-text");

        // Chỉ add title và content vào uiBox, buttons sẽ ở buttonOverlay
        uiBox.getChildren().addAll(title, contentBox);
    }

    // -----------------------------
    // Page 1: Highest Scores
    // -----------------------------
    public void showPage1(List<Integer> scores) {
        showingPage1 = true;
        contentBox.getChildren().clear();
        buttonOverlay.getChildren().clear(); // Clear buttons cũ


        Text pageTitle = new Text("🏆 Highest Scores (Top 10)");
        pageTitle.getStyleClass().add("subtitle-text");
        pageTitle.setStyle("-fx-fill: white;");

        VBox scoreBox = new VBox(8);
        scoreBox.setAlignment(Pos.CENTER);
        for (Integer score : scores) {
            System.out.println(score);
        }

        // --- START: LOGIC MỚI CHO 10 BOX ---
        final int MAX_BOXES = 10;

        for (int i = 0; i < MAX_BOXES; i++) {
            String displayText;

            if (i < scores.size()) {
                // Hiển thị điểm thật
                displayText = (i + 1) + ". " + scores.get(i);
            } else {
                // Hiển thị box trống nếu không đủ điểm
                displayText = (i + 1) + ". - - -";
                // Hoặc chỉ hiển thị số thứ tự và để trống nếu bạn muốn:
                // displayText = (i + 1) + ". ";
            }

            Text scoreText = new Text(displayText);
            scoreText.setStyle("-fx-fill: white;");
            scoreBox.getChildren().add(scoreText);
        }
        // --- END: LOGIC MỚI CHO 10 BOX ---


        contentBox.getChildren().addAll(pageTitle, scoreBox);

        // Tạo buttons - vị trí đã được set tự động trong ButtonManager
        var backBtn = buttonManager.createButtonRowPosition("Back to Menu", e -> controller.backToMenu(), 0, 740);
        var nextBtn = buttonManager.createButtonRowPosition("Next Page →", e -> controller.nextPage(), 325, 740);

        buttonOverlay.getChildren().addAll(backBtn, nextBtn);
    }

    // -----------------------------
    // Page 2: Achievements
    // -----------------------------
    public void showPage2(List<Achievement> achievements) {
        showingPage1 = false;
        contentBox.getChildren().clear();
        buttonOverlay.getChildren().clear(); // Clear buttons cũ

        Text pageTitle = new Text("⭐ Achievements");
        pageTitle.getStyleClass().add("subtitle-text");

        VBox listBox = new VBox(10);
        listBox.setAlignment(Pos.CENTER);
        for (Achievement a : achievements) {
            String status = a.isUnlocked() ? "✔️" : "❌";
            Text item = new Text(status + " " + a.getName());
            listBox.getChildren().add(item);
        }

        contentBox.getChildren().addAll(pageTitle, listBox);

        // Tạo buttons - vị trí đã được set tự động trong ButtonManager
        var prevBtn = buttonManager.createButtonRowPosition("← Previous Page", e -> controller.prevPage(), 0, 740);
        var backBtn = buttonManager.createButtonRowPosition("Back to Menu", e -> controller.backToMenu(), 325, 740);

        buttonOverlay.getChildren().addAll(prevBtn, backBtn);
    }

    // -----------------------------
    // InputHandler
    // -----------------------------
    @Override
    public void handleInput(KeyCode code) {
        switch (code) {
            case LEFT -> { if (!showingPage1) controller.prevPage(); }
            case RIGHT -> { if (showingPage1) controller.nextPage(); }
            case ENTER -> selectionController.confirm();
            case ESCAPE -> controller.backToMenu();
        }
    }

    @Override
    public void moveUp() { }
    @Override
    public void moveDown() { }
    @Override
    public void moveLeft() { }
    @Override
    public void moveRight() { }
    @Override
    public void confirm() { selectionController.confirm(); }
    @Override
    public void cancel() { controller.backToMenu(); }

    private void handleConfirm(String option) {
        if ("Back to Menu".equals(option)) controller.backToMenu();
    }
}