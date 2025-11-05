package com.example.demo;

<<<<<<< Updated upstream
import com.example.demo.controller.GameManager;
import com.example.demo.controller.MenuControll;
import com.example.demo.controller.SettingsControllers;
<<<<<<< Updated upstream
import com.example.demo.model.map.MenuModel;
import com.example.demo.model.map.SettingsModel;
=======
import com.example.demo.controller.SlotSelectionController;
=======
import com.example.demo.controller.core.GameController;
import com.example.demo.controller.map.MenuController;
import com.example.demo.controller.view.SettingsController;
import com.example.demo.controller.view.SlotSelectionController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.model.assets.AssetManager;
import com.example.demo.model.menu.ButtonManager;
>>>>>>> Stashed changes
import com.example.demo.model.menu.MenuModel;
import com.example.demo.model.menu.SettingsModel;
import com.example.demo.model.state.GameState;
>>>>>>> Stashed changes
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Input;
import com.example.demo.model.utils.Sound;
import com.example.demo.view.MenuView;
import com.example.demo.view.SlotSelectionView;
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

    private SlotSelectionView slotSelectionView;
    private SlotSelectionController slotSelectionController;

    private StackPane mainRoot;
    private Scene mainScene;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // --- Khởi tạo Models ---
        menuModel = new MenuModel();
        settingsModel = new SettingsModel();

        initSlotSelection();

        // --- Khởi tạo Controllers ---
        MenuControll menuController = new MenuControll(menuModel);
        SettingsControllers settingsController = new SettingsControllers(settingsModel, menuModel);

        ThemeController themeController = new ThemeController();
        ButtonManager buttonManager = new ButtonManager(themeController.getHandImage());
        // --- Khởi tạo Views ---
        menuView = new MenuView(menuController,themeController,buttonManager);
        settingsView = new SettingsView(settingsController);

        // --- Tạo root chính chứa các màn hình ---
        mainRoot = new StackPane(menuView.getRoot());
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
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
        mainRoot.getChildren().clear();

        switch (screen) {
            case MENU -> showMenu();
            case SETTINGS -> showSettings();
            case SELECT -> showSlotSelection();
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

    private void showSlotSelection() {
        mainRoot.getChildren().add(slotSelectionView.getRoot());
        slotSelectionView.enableKeyboard(mainScene);
        slotSelectionView.getRoot().requestFocus();
    }

    // ========== FIXED METHODS ==========

    private void startNewGame(int slotNumber) {
        System.out.println("Starting NEW GAME in slot " + slotNumber);

        gameManager = new GameManager();
        gameManager.setNewGame(true);  // ← QUAN TRỌNG: Đánh dấu là New Game
        gameManager.setCurrentSlot(slotNumber);

        menuModel.setCurrentScreen(MenuModel.Screen.PLAY);
    }

    private void continueGame(int slotNumber, GameState gameState) {
        System.out.println("LOADING GAME from slot " + slotNumber);

        gameManager = new GameManager();
        gameManager.setNewGame(false);  // ← QUAN TRỌNG: Đánh dấu là Load Game
        gameManager.setCurrentSlot(slotNumber);
        gameManager.applyState(gameState);  // Apply sau khi set slot

        menuModel.setCurrentScreen(MenuModel.Screen.PLAY);
    }

    private void initSlotSelection() {
        slotSelectionController = new SlotSelectionController();
        slotSelectionView = new SlotSelectionView(slotSelectionController);

        // Setup callbacks
        slotSelectionController.setOnBackToMenu(() -> {
            menuModel.setCurrentScreen(MenuModel.Screen.MENU);
        });

        slotSelectionController.setOnStartGame((slotNumber, gameState) -> {
            if (gameState == null) {
                // New game → chạy intro dialogue
                startNewGame(slotNumber);
            } else {
                // Continue game → KHÔNG chạy intro dialogue
                continueGame(slotNumber, gameState);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
