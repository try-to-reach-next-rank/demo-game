package com.example.demo.view;

import com.example.demo.controller.core.GameController;
import com.example.demo.utils.CheatTable;
import com.example.demo.utils.PauseTable;
import com.example.demo.utils.dialogue.DialogueBox;
import com.example.demo.utils.dialogue.DialogueSystem;
import com.example.demo.view.ui.UIComponent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

public class UIView {
    private final UIManager uiManager;
    private final PauseTable pauseTable;
    private final CheatTable cheatTable;
    private final StackPane root;
    private final DialogueBox dialogueBox;
    private DialogueSystem dialogueSystem;
    GameController gameController;

    public UIView(GameController gameController) {
        this.uiManager = new UIManager();
        this.pauseTable = new PauseTable(gameController);
        this.cheatTable = new CheatTable(gameController);
        this.dialogueBox = new DialogueBox();

        boolean isNewGame = gameController.GetIsNewGame();
        this.gameController = gameController;

        root = new StackPane();
        root.getChildren().addAll(
                pauseTable.getView(),
                cheatTable.getView()
        );

        uiManager.add(dialogueBox);
        uiManager.add(pauseTable);
        uiManager.add(cheatTable);
    }

    public StackPane getView() {
        return root;
    }

    public void render(GraphicsContext gc, double width, double height) {
        uiManager.render(gc, width, height);
    }

    public void handleInput(KeyCode code) {
        uiManager.handleInput(code);

        switch (code) {
            case Q -> showPause();
            case BACK_QUOTE -> {
                if (this.gameController.getAllowToCheat()) {
                    showCheat();
                }
            }
            default -> {}
        }
    }

    public void showPause() {
        hideAll();
        pauseTable.show();
    }

    public void showCheat() {
        hideAll();
        cheatTable.show();
    }

    public void loadDialogue(String path) {
        this.dialogueSystem = new DialogueSystem(path, dialogueBox);
    }

    public void startDialogue() {
        if (dialogueSystem != null) {
            dialogueSystem.start();
        }
    }

    public void resumeDialogue() {
        if (dialogueBox != null) {
            dialogueBox.resumeDialogue();
        }
    }

    public void hideAll() {
        for (UIComponent comp : getAllUI()) {
            comp.hide();
        }
    }

    public void update(double deltaTime) {
        uiManager.update(deltaTime);
    }

    public void reset() {
        hideAll();
    }

    private UIComponent[] getAllUI() {
        return new UIComponent[]{pauseTable, cheatTable, dialogueBox};
    }

    public boolean hasActiveUI() {
        return uiManager.hasActiveUI();
    }

    public StackPane getRoot() {return root;}

    public boolean isDialogueActive() {
        return dialogueBox.isActive();
    }
}
