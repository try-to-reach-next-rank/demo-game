package com.example.demo;

import com.example.demo.controller.core.GameController;
import com.example.demo.controller.map.MenuController;
import com.example.demo.controller.view.SettingsController;
import com.example.demo.controller.view.SlotSelectionController;
import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.menu.MenuModel;
import com.example.demo.model.menu.SettingsModel;
import com.example.demo.model.state.GameState;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.AssetPaths;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.GameView;
import com.example.demo.view.ui.MenuView;
import com.example.demo.view.ui.SlotSelectionView;
import com.example.demo.view.ui.SettingsView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private MenuModel menuModel;

    private GameController gameController;
    private MenuView menuView;
    private SettingsView settingsView;
    private SlotSelectionView slotSelectionView;

    private StackPane mainRoot;
    private Scene mainScene;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // --- Khởi tạo Models ---
        menuModel = new MenuModel();
        SettingsModel settingsModel = new SettingsModel();

        // --- Khởi tạo Slot Selection trước ---
        initSlotSelection();

        // --- Khởi tạo Controllers ---
        MenuController menuController = new MenuController(menuModel);
        SettingsController settingsController = new SettingsController(settingsModel, menuModel);

        // --- Khởi tạo Views ---
        menuView = new MenuView(menuController);
        settingsView = new SettingsView(settingsController);

        // --- Tạo root chính chứa các màn hình ---
        mainRoot = new StackPane(MenuView.getRoot());
        mainScene = new Scene(mainRoot, GlobalVar.WIDTH, GlobalVar.HEIGHT);

        // --- Gắn sự kiện bàn phím cho menu ---
        menuView.enableKeyboard(mainScene);

        // --- Theo dõi thay đổi màn hình ---
        menuModel.currentScreenProperty().addListener((obs, oldScreen, newScreen) -> {
            switchScreen(newScreen);
        });

        // --- Cấu hình Stage ---
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource(AssetPaths.ICON)).toExternalForm()));
        stage.setTitle("Brick Breaker");
        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();

        // --- Load all resources ---
        AssetManager.getInstance().loadAll();

        // --- Nhạc nền ---
        Sound.getInstance().loopMusic("Hametsu-no-Ringo");
    }

    // -------------------------------------------------------------------------
    //  Slot Selection Initialization
    // -------------------------------------------------------------------------

    private void initSlotSelection() {
        SlotSelectionController slotSelectionController = new SlotSelectionController();
        slotSelectionView = new SlotSelectionView(slotSelectionController);

        // Setup callback: Quay về menu
        slotSelectionController.setOnBackToMenu(() -> {
            menuModel.setCurrentScreen(MenuModel.Screen.MENU);
        });

        // Setup callback: Start game từ slot
        slotSelectionController.setOnStartGame((slotNumber, gameState) -> {
            if (gameState == null) {
                // NEW GAME
                startNewGame(slotNumber);
            } else {
                // LOAD GAME
                continueGame(slotNumber, gameState);
            }
        });
    }

    // -------------------------------------------------------------------------
    //  Screen Switching
    // -------------------------------------------------------------------------

    private void switchScreen(MenuModel.Screen screen) {
        mainRoot.getChildren().clear();

        switch (screen) {
            case MENU -> showMenu();
            case SETTINGS -> showSettings();
            case SELECT -> showSlotSelection();  // ← Hiện slot selection
            case PLAY -> showGame();
            case EXIT -> primaryStage.close();
        }
    }

    private void showMenu() {
        mainRoot.getChildren().add(MenuView.getRoot());
        menuView.enableKeyboard(mainScene);

        Sound.getInstance().stopMusic();
        Sound.getInstance().loopMusic("Hametsu-no-Ringo");

        MenuView.getRoot().requestFocus();
    }

    private void showSettings() {
        mainRoot.getChildren().add(settingsView.getRoot());
        settingsView.enableKeyboard(mainScene);
        settingsView.getRoot().requestFocus();
    }

    private void showSlotSelection() {
        slotSelectionView.refreshSlots();
        mainRoot.getChildren().add(SlotSelectionView.getRoot());
        slotSelectionView.enableKeyboard(mainScene);
        SlotSelectionView.getRoot().requestFocus();
    }

    private void showGame() {
        mainRoot.getChildren().setAll(gameController);
        gameController.requestFocus();
    }

    // -------------------------------------------------------------------------
    //  Game Start Methods
    // -------------------------------------------------------------------------

    private void startNewGame(int slotNumber) {
        gameController = new GameController();
        gameController.setNewGame(true);           // ← Đánh dấu New Game
        gameController.setCurrentSlot(slotNumber);
        gameController.setOnBackToMenu(() -> {
            menuModel.setCurrentScreen(MenuModel.Screen.MENU);
        });

        mainRoot.getChildren().setAll(gameController);
        gameController.initGame();
        gameController.requestFocus();

        gameController.startIntroDialogue();

        menuModel.setCurrentScreen(MenuModel.Screen.PLAY);
    }

    private void continueGame(int slotNumber, GameState gameState) {
        Main.log.info("=== LOADING GAME from slot {} ===", slotNumber);

        gameController = new GameController();
        gameController.setNewGame(false);          // ← Đánh dấu Load Game
        gameController.setCurrentSlot(slotNumber); // ← Set slot
        gameController.setOnBackToMenu(() -> menuModel.setCurrentScreen(MenuModel.Screen.MENU));

        gameController.initGame();
        gameController.applyState(gameState);      // ← Apply saved state

        mainRoot.getChildren().setAll(gameController);
        gameController.requestFocus();

        menuModel.setCurrentScreen(MenuModel.Screen.PLAY);
    }
}