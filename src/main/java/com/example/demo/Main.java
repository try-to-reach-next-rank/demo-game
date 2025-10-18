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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private MenuModel menuModel;
    private SettingsModel settingsModel;

    private GameManager gameManager;
    private MenuView menuView;
    private SettingsView settingsView;

    private Scene menuScene;
    private Scene settingsScene;
    private Scene currentScene;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Khởi tạo Models
        menuModel = new MenuModel();
        settingsModel = new SettingsModel();

        // Khởi tạo Controllers
        MenuControll menuController = new MenuControll(menuModel);
        SettingsControllers settingsController = new SettingsControllers(settingsModel, menuModel);

        // Khởi tạo Views
        menuView = new MenuView(menuController);
        settingsView = new SettingsView(settingsController);

        menuScene = new Scene((Parent) menuView.getRoot(), GlobalVar.WIDTH, GlobalVar.HEIGHT);
        settingsScene = new Scene((Parent) settingsView.getRoot(), GlobalVar.WIDTH, GlobalVar.HEIGHT);

        menuView.enableKeyboard(menuScene);
        settingsView.enableKeyboard(settingsScene);

        // Listen changes
        menuModel.currentScreenProperty().addListener((obs, oldScreen, newScreen) -> {
            switchScreen(newScreen);
        });

        // Bắt đầu với Menu
        currentScene = menuScene;
        stage.getIcons().add(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
        stage.setTitle("Brick Breaker");
        stage.setScene(menuScene);
        stage.setResizable(false);
        stage.show();

        Sound.getInstance().loopMusic("Hametsu-no-Ringo");
    }

    private void switchScreen(MenuModel.Screen screen) {
        switch (screen) {
            case MENU:
                showMenu();
                break;
            case SETTINGS:
                showSettings();
                break;
            case PLAY:
                showGame();
                break;
            case GUIDE:
                System.out.println("Guide not implemented");
                menuModel.setCurrentScreen(MenuModel.Screen.MENU);
                break;
            case EXIT:
                primaryStage.close();
                break;
        }
    }

    private void showMenu() {
        primaryStage.setScene(menuScene);
        Sound.getInstance().stopMusic();
        Sound.getInstance().loopMusic("Hametsu-no-Ringo");
        menuView.getRoot().requestFocus();
    }

    private void showSettings() {
        primaryStage.setScene(settingsScene);
        settingsView.getRoot().requestFocus();
    }

    private void showGame() {
        gameManager = new GameManager();
        Input input = new Input(gameManager.getPaddle(), gameManager.getBall());

        currentScene = new Scene(gameManager, GlobalVar.WIDTH, GlobalVar.HEIGHT);

        currentScene.setOnKeyPressed(e -> {
            if (gameManager.getUIManager().hasActiveUI()) {
                gameManager.getUIManager().handleInput(e.getCode());
            } else {
                input.handleKeyPressed(e.getCode());
            }
        });

        currentScene.setOnKeyReleased(e -> {
            if (!gameManager.getUIManager().hasActiveUI()) {
                input.handleKeyReleased(e.getCode());
            }
        });

        primaryStage.setScene(currentScene);
        gameManager.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}