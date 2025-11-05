package com.example.demo.controller.view;

import com.example.demo.model.menu.SaveSlot;
import com.example.demo.model.menu.SaveSlotManager;
import com.example.demo.model.state.GameState;
import com.example.demo.repository.SaveDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SlotSelectionController {
    private static final Logger log = LoggerFactory.getLogger(SlotSelectionController.class);
    private final SaveSlotManager slotManager;
    private final SaveDataRepository repository;

    private Runnable onBackToMenu;
    private SlotGameStartCallback onStartGame;

    @FunctionalInterface
    public interface SlotGameStartCallback {
        void startGame(int slotNumber, GameState gameState);
    }

    public SlotSelectionController() {
        this.slotManager = new SaveSlotManager();
        this.repository = slotManager.getRepository();
        slotManager.loadAllSlots();
    }

    // Setters
    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void setOnStartGame(SlotGameStartCallback callback) {
        this.onStartGame = callback;
    }

    // Queries
    public List<SaveSlot> getAllSlots() {
        return slotManager.getAllSlots();
    }

    // Actions
    public void handleNewGame(int slotNumber) {
        log.info("Starting new game in slot {}", slotNumber);
        if (onStartGame != null) {
            onStartGame.startGame(slotNumber, null);  // ← null = New Game
        }
    }

    public void handleContinueGame(int slotNumber) {
        log.info("Loading game from slot {}", slotNumber);
        GameState gameState = repository.loadSlot(slotNumber);

        if (gameState == null) {
            SlotSelectionController.log.info("Failed to load slot {}", slotNumber);
            return;
        }

        if (onStartGame != null) {
            onStartGame.startGame(slotNumber, gameState);  // ← có gameState = Load Game
        }
    }

    public void handleDeleteSlot(int slotNumber) {
        log.info("Deleting slot {}", slotNumber);
        boolean deleted = repository.deleteSlot(slotNumber);

        if (deleted) {
            slotManager.reloadSlot(slotNumber);
            log.info("Slot {} deleted", slotNumber);
        }
    }

    public void handleBackToMenu() {
        log.info("Back to menu");
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }

    public void refreshSlots() {
        slotManager.reloadAll();
    }
}