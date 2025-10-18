package com.example.demo;

import com.example.demo.controller.GameManager;
import com.example.demo.controller.MenuControll;
import com.example.demo.controller.SettingsControllers;
import com.example.demo.model.map.MenuModel;
import com.example.demo.model.map.SettingsModel;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Input;
import com.example.demo.model.utils.Sound;
import com.example.demo.view.MenuView;
import com.example.demo.view.ui.SettingsView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    private MenuModel menuModel;
    private SettingsModel settingsModel;

    private GameManager gameManager;
    private MenuView menuView;
    private SettingsView settingsView;

    private StackPane mainRoot;
    private Scene mainScene;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // --- Khởi tạo Models ---
        menuModel = new MenuModel();
        settingsModel = new SettingsModel();

        // --- Khởi tạo Controllers ---
        MenuControll menuController = new MenuControll(menuModel);
        SettingsControllers settingsController = new SettingsControllers(settingsModel, menuModel);

        // --- Khởi tạo Views ---
        menuView = new MenuView(menuController);
        settingsView = new SettingsView(settingsController);

        // --- Tạo root chính chứa các màn hình ---
        mainRoot = new StackPane(menuView.getRoot()); // bắt đầu với menu
        mainScene = new Scene(mainRoot, GlobalVar.WIDTH, GlobalVar.HEIGHT);

        // --- Gắn sự kiện bàn phím cho menu ---
        menuView.enableKeyboard(mainScene);

        // --- Theo dõi thay đổi màn hình ---
        menuModel.currentScreenProperty().addListener((obs, oldScreen, newScreen) -> {
            switchScreen(newScreen);
        });

        // --- Cấu hình Stage ---
        stage.getIcons().add(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
        stage.setTitle("Brick Breaker");
        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();

        // --- Nhạc nền ---
        Sound.getInstance().loopMusic("Hametsu-no-Ringo");
    }

    // --- Chuyển giữa các màn hình ---
    private void switchScreen(MenuModel.Screen screen) {
        mainRoot.getChildren().clear(); // xoá nội dung cũ trước khi thêm mới

        switch (screen) {
            case MENU -> showMenu();
            case SETTINGS -> showSettings();
            case PLAY -> showGame();
            case EXIT -> primaryStage.close();
        }
    }

    private void showMenu() {
        mainRoot.getChildren().add(menuView.getRoot());
        menuView.enableKeyboard(mainScene);

        Sound.getInstance().stopMusic();
        Sound.getInstance().loopMusic("Hametsu-no-Ringo");

        menuView.getRoot().requestFocus();
    }

    private void showSettings() {
        mainRoot.getChildren().add(settingsView.getRoot());
        settingsView.enableKeyboard(mainScene);
        settingsView.getRoot().requestFocus();
    }

    private void showGame() {
        gameManager = new GameManager();
        Input input = new Input(gameManager.getPaddle(), gameManager.getBall());

        mainRoot.getChildren().add(gameManager);

        mainScene.setOnKeyPressed(e -> {
            if (gameManager.getUIManager().hasActiveUI()) {
                gameManager.getUIManager().handleInput(e.getCode());
            } else {
                input.handleKeyPressed(e.getCode());
            }
        });

        mainScene.setOnKeyReleased(e -> {
            if (!gameManager.getUIManager().hasActiveUI()) {
                input.handleKeyReleased(e.getCode());
            }
        });

        gameManager.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}