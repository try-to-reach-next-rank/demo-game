package com.example.demo.view.ui;

import com.example.demo.controller.view.AchievementController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.ui.AbstractUIView;
import com.example.demo.engine.ui.UISelectionController;
import com.example.demo.model.menu.Achievement;
import com.example.demo.model.menu.ButtonManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;


import javafx.scene.control.Label;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class AchievementView extends AbstractUIView {

    private final AchievementController controller;
    private final VBox uiBox;
    private final ButtonManager buttonManager;
    private final UISelectionController selectionController;

    private final VBox contentBox;
    private final Pane buttonOverlay;

    private boolean showingPage1 = true;
    private final Image scoreBackground;
    private final Image loockedAchievement;
    private final Image wingameimg;
    private final Image eastereggimg;


    public AchievementView(AchievementController controller) {
        this.controller = controller;
        this.buttonManager = new ButtonManager(themeController.getHandImage());

        scoreBackground = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/score.png")));
        loockedAchievement = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/locked_achie.png")));
        wingameimg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/wingame.png")));
        eastereggimg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/easteregg.png")));

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

        VBox scoreBox = new VBox(10);
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
        VBox listBox = new VBox(50);
        listBox.setAlignment(Pos.CENTER);
        for (Achievement a : achievements) {
            VBox achievementPane = CreateAchievementBox(a);
            listBox.getChildren().add(achievementPane);
        }



        contentBox.getChildren().addAll(pageTitle, listBox);

        var prevBtn = buttonManager.createButtonRowPosition("← Previous Page", e -> controller.prevPage(), 0, 740);
        var backBtn = buttonManager.createButtonRowPosition("Back to Menu", e -> controller.backToMenu(), 325, 740);

        buttonOverlay.getChildren().addAll(prevBtn, backBtn);
    }

    private VBox CreateAchievementBox(Achievement achievement) {

        VBox box = new VBox();
        box.setPrefSize(150, 100);
        box.setMinSize(150, 100);
        box.setMaxSize(150, 100);
        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(4);

        ImageView Achive = null;

        if (!achievement.isUnlocked()) {
            Achive = new ImageView(loockedAchievement);

        } else {
            if ("Win Level 1".equals(achievement.getName())) {
                Achive = new ImageView(loockedAchievement);
            } else if ("Win Level 2".equals(achievement.getName())) {
                Achive = new ImageView(loockedAchievement);
            } else if ("Win Game".equals(achievement.getName())) {
                Achive = new ImageView(wingameimg);
            } else {
                Achive = new ImageView(eastereggimg);
            }
        }

        Achive.setFitWidth(150);
        Achive.setFitHeight(100);
        Achive.setPreserveRatio(false);
        Achive.setSmooth(true);
        Label nameTag;
        ImageView labelImg = new ImageView(scoreBackground);
        if(!achievement.isUnlocked()) {
            nameTag = new Label("?????");
        }else {
            nameTag = new Label(achievement.getName());
        }
        nameTag.setGraphic(labelImg);
        nameTag.setContentDisplay(ContentDisplay.CENTER);
        nameTag.setMaxWidth(Double.MAX_VALUE);
        nameTag.setAlignment(Pos.CENTER);
        nameTag.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: rgba(0, 0, 0, 0.6);" +
                        "-fx-padding: 4;"
        );

        box.getChildren().addAll(Achive, nameTag);

        return box;
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