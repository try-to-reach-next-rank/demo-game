package com.example.demo.model.stage;

import com.example.demo.Main;
import com.example.demo.controller.map.MenuController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.state.GameState;
import com.example.demo.model.menu.MenuModel;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.ui.MenuView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuState implements GameState {

    private final Main mainApp;
    private final Stage stage;
    private final StackPane root;

    private MenuModel model;
    private MenuController controller;
    private MenuView view;

    private ThemeController themeController;
    private Scene scene;

    public MenuState(Main mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.root = new StackPane();
    }

    @Override
    public void enter() {
        // --- Khởi tạo MVC ---
        model = new MenuModel();
        themeController = new ThemeController();
        controller = new MenuController(model);

        view = new MenuView(controller, themeController); // <-- ĐÃ SỬA

        // --- Gắn scene ---
        root.getChildren().setAll(view.getRoot());
        scene = new Scene(root, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        view.enableKeyboard(scene);

        stage.setScene(scene);
        stage.show();

        // --- Nhạc nền ---
        String currentTrack = Sound.getInstance().getCurrentTrackName();
        if (currentTrack == null || !currentTrack.equals("Hametsu-no-Ringo")) {
            Sound.getInstance().stopMusic();
            Sound.getInstance().loopMusic("Hametsu-no-Ringo");
        }

        // --- Lắng nghe chuyển màn ---
        model.currentScreenProperty().addListener((obs, oldScreen, newScreen) -> {
            switch (newScreen) {
                case SETTINGS -> mainApp.getStateManager().changeState(new SettingsState(mainApp, stage));

                case SELECT -> mainApp.getStateManager().changeState(new SlotSelectionState(mainApp, stage));

                case ACHIEVEMENT -> mainApp.getStateManager().changeState(new AchievementState(mainApp, stage));

                case EXIT -> stage.close();

                default -> {}
            }
        });
    }


    @Override
    public void exit() {
       //  Sound.getInstance().pauseMusic();
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}