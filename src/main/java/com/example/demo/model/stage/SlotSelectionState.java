// File: SlotSelectionState.java (Đã sửa và hoàn thiện)

package com.example.demo.model.stage;

import com.example.demo.Main;
import com.example.demo.controller.view.SlotSelectionController;
import com.example.demo.controller.view.ThemeController;
import com.example.demo.engine.state.GameState;
import com.example.demo.model.menu.ButtonManager;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.ui.SlotSelectionView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SlotSelectionState implements GameState {

    private final Main mainApp;
    private final Stage stage;
    private final StackPane root;
    private Scene scene;

    private SlotSelectionController slotSelectionController;

    public SlotSelectionState(Main mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.root = new StackPane();
    }

    @Override
    public void enter() {
        // --- Khởi tạo MVC
        ThemeController themeController = new ThemeController();
        ButtonManager buttonManager = new ButtonManager(themeController.getHandImage());

        slotSelectionController = new SlotSelectionController();

        SlotSelectionView slotSelectionView = new SlotSelectionView(slotSelectionController, themeController, buttonManager);

        // --- Gắn Scene
        root.getChildren().setAll(slotSelectionView.getRoot());
        scene = new Scene(root, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        slotSelectionView.enableKeyboard(scene);
        stage.setScene(scene);
        stage.show();

        // --- Call back:

        //  về Menu
        slotSelectionController.setOnBackToMenu(() -> {
            System.out.println("DEBUG (SlotState): Nhận BACK. Chuyển về MenuState.");
            mainApp.getStateManager().changeState(new MenuState(mainApp, stage));
        });

        // vào game
        slotSelectionController.setOnStartGame((slotNumber, gameState) -> {
            mainApp.getStateManager().changeState( new GamePlayState(mainApp, stage, slotNumber, gameState));
        });
        slotSelectionView.refreshSlots();
        slotSelectionView.getRoot().requestFocus();
    }

    @Override
    public void exit() {
        if (slotSelectionController != null) {
            slotSelectionController.setOnBackToMenu(null);
            slotSelectionController.setOnStartGame(null);
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}