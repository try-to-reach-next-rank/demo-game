package com.example.demo.model.stage;

import com.example.demo.Main;
import com.example.demo.controller.view.AchievementController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.GameWorld;
import com.example.demo.engine.state.GameState;
import com.example.demo.model.menu.AchievementModel;
import com.example.demo.model.state.highscore.HighScoreState;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.ui.AchievementView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AchievementState implements GameState {

    private final Main mainApp;
    private final Stage stage;
    private final StackPane root;
    private Scene scene;

    private AchievementView achievementView;
    private AchievementController achievementController;
    private ThemeController themeController;

    GameWorld gameWorld;

    public AchievementState(Main mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.root = new StackPane();
    }

    @Override
    public void enter() {
        // --- Khởi tạo MVC ---
        HighScoreState highScoreState = new HighScoreState().getInstance();
        AchievementModel achievementModel = new AchievementModel(highScoreState);

        achievementController = new AchievementController(achievementModel);

        themeController = new ThemeController();

        achievementView = new AchievementView(achievementController, themeController);

        // --- Hiển thị trang đầu tiên ---
        achievementView.showPage1(achievementModel.getHighestScores());

        // --- Gắn Scene ---
        root.getChildren().setAll(achievementView.getRoot());
        scene = new Scene(root, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        achievementView.enableKeyboard(scene);
        stage.setScene(scene);
        stage.show();

        // --- Gắn callback ---
        achievementController.setOnNextPage(() ->
                achievementView.showPage2(achievementModel.getAchievements())
        );

        achievementController.setOnPrevPage(() ->
                achievementView.showPage1(achievementModel.getHighestScores())
        );

        achievementController.setOnBackToMenu(() ->
                mainApp.getStateManager().changeState(new MenuState(mainApp, stage))
        );

        achievementView.getRoot().requestFocus();
    }

    @Override
    public void exit() {
        if (achievementController != null) {
            achievementController.setOnBackToMenu(null);
            achievementController.setOnNextPage(null);
            achievementController.setOnPrevPage(null);
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }
}
