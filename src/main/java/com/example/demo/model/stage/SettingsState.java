package com.example.demo.model.stage;

import com.example.demo.Main;
import com.example.demo.controller.view.SettingsController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.state.GameState;
import com.example.demo.model.menu.SettingsModel;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.ui.SettingsView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SettingsState implements GameState {

    private final Main mainApp;
    private final Stage stage;
    private final StackPane root;
    private Scene scene;

    private SettingsView settingsView;
    private SettingsController settingsController;


    public SettingsState(Main mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.root = new StackPane();
    }

    @Override
    public void enter() {
        // --Khởi tạo MVC
        SettingsModel settingsModel = new SettingsModel();
        ThemeController themeController = new ThemeController();

        settingsController = new SettingsController(settingsModel);
        settingsView = new SettingsView(settingsController, themeController);

        // --- Gắn Scene ---
        root.getChildren().setAll(settingsView.getRoot());
        scene = new Scene(root, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        settingsView.enableKeyboard(scene);
        stage.setScene(scene);
        stage.show();

        settingsController.setOnBackToMenu(() -> {
            System.out.println("DEBUG P2: SettingsState ĐÃ nhận callback BACK TO MENU.");
            mainApp.getStateManager().changeState(new MenuState(mainApp, stage));
        });

        settingsView.getRoot().requestFocus();
    }

    @Override
    public void exit() {
        if (settingsController != null) {
            settingsController.setOnBackToMenu(null);
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}