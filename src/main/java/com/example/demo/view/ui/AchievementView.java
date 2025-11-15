package com.example.demo.view.ui;

import com.example.demo.controller.view.AchievementController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.ui.AbstractUIView;
import com.example.demo.engine.ui.UISelectionController;
import com.example.demo.model.menu.Achievement;
import com.example.demo.model.menu.ButtonManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private final Pane buttonOverlay;

    private boolean showingPage1 = true;
    private Image scoreBackground;

    public AchievementView(AchievementController controller, ThemeController themeController) {
        super(themeController);
        this.controller = controller;
        this.buttonManager = new ButtonManager(themeController.getHandImage());

        scoreBackground = new Image(getClass().getResourceAsStream("/images/score.png"));

        this.uiBox = new VBox(20);
        this.uiBox.setPadding(new Insets(10, 30, 30, 30)); // top = 10 để đẩy lên cao
        this.uiBox.setAlignment(Pos.TOP_CENTER); // Đẩy lên trên

        this.contentBox = new VBox(15); // Spacing vừa phải
        contentBox.setAlignment(Pos.CENTER);

        this.buttonOverlay = new Pane();
        buttonOverlay.setPickOnBounds(false);

        buildUI();

        this.selectionController = new UISelectionController(List.of("Back to Menu"));
        selectionController.setOnConfirm(this::handleConfirm);

        root.getChildren().addAll(uiBox, buttonOverlay);
        StackPane.setAlignment(uiBox, Pos.CENTER);
    }

    @Override
    public void buildUI() {
        uiBox.getChildren().addAll(contentBox);
    }


    //  --- page 1
    public void showPage1(List<Integer> scores) {
        showingPage1 = true;
        contentBox.getChildren().clear();
        buttonOverlay.getChildren().clear();

        Text pageTitle = new Text("🏆 Highest Scores ");
        pageTitle.getStyleClass().add("subtitle-text");
        pageTitle.setStyle("-fx-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;"); // Tăng size và bold

        VBox scoreBox = new VBox(10); // Spacing giữa các box
        scoreBox.setAlignment(Pos.CENTER);

        final int MAX_BOXES = 10;

        for (int i = 0; i < MAX_BOXES; i++) {
            StackPane scorePane = createScoreBox(i, i < scores.size() ? scores.get(i) : null);
            scoreBox.getChildren().add(scorePane);
        }

        contentBox.getChildren().addAll(pageTitle, scoreBox);

        var backBtn = buttonManager.createButtonRowPosition("Back to Menu", e -> controller.backToMenu(), 0, 740);
        var nextBtn = buttonManager.createButtonRowPosition("Next Page →", e -> controller.nextPage(), 325, 740);

        buttonOverlay.getChildren().addAll(backBtn, nextBtn);
    }

    /**
     * tạo score box
     */
    private StackPane createScoreBox(int rank, Integer score) {
        StackPane pane = new StackPane();
        pane.setPrefSize(300, 50);
        pane.setMaxSize(300, 50);
        pane.setMinSize(300, 50);

        ImageView bgView = new ImageView(scoreBackground);
        bgView.setFitWidth(300);
        bgView.setFitHeight(50);
        bgView.setPreserveRatio(false);
        bgView.setSmooth(true);
        pane.getChildren().add(bgView);

        // Tạo text hiển thị
        HBox textBox = new HBox(15);
        textBox.setAlignment(Pos.CENTER);
        textBox.setPadding(new Insets(5));

        Text rankText = new Text((rank + 1) + ".");
        rankText.setStyle("-fx-fill: gold; -fx-font-size: 18px; -fx-font-weight: bold;");

        Text scoreText;
        if (score != null) {
            scoreText = new Text(String.valueOf(score));
            scoreText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        } else {
            scoreText = new Text("- - -");
            scoreText.setStyle("-fx-fill: gray; -fx-font-size: 18px; -fx-font-style: italic;");
        }

        textBox.getChildren().addAll(rankText, scoreText);
        pane.getChildren().add(textBox);

        return pane;
    }

    /**
     * page 2
     */
    public void showPage2(List<Achievement> achievements) {
        showingPage1 = false;
        contentBox.getChildren().clear();
        buttonOverlay.getChildren().clear();

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

        var prevBtn = buttonManager.createButtonRowPosition("← Previous Page", e -> controller.prevPage(), 0, 740);
        var backBtn = buttonManager.createButtonRowPosition("Back to Menu", e -> controller.backToMenu(), 325, 740);

        buttonOverlay.getChildren().addAll(prevBtn, backBtn);
    }

   /**
    * các handle
    */
    @Override
    public void handleInput(KeyCode code) {
        switch (code) {
            case LEFT -> { if (!showingPage1) controller.prevPage(); }
            case RIGHT -> { if (showingPage1) controller.nextPage(); }
            case ENTER -> selectionController.confirm();
            case ESCAPE -> controller.backToMenu();
            default -> {}
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