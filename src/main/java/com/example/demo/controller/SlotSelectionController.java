package com.example.demo.controller;

import com.example.demo.model.menu.SaveSlot;
import com.example.demo.model.menu.SaveSlotManager;
import com.example.demo.model.state.GameState;
import com.example.demo.repository.SaveDataRepository;

import java.util.List;

public class SlotSelectionController {
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
        System.out.println("[SlotSelectionController] Starting new game in slot " + slotNumber);
        if (onStartGame != null) {
            onStartGame.startGame(slotNumber, null);  // ← null = New Game
        }
    }

    public void handleContinueGame(int slotNumber) {
        System.out.println("[SlotSelectionController] Loading game from slot " + slotNumber);
        GameState gameState = repository.loadSlot(slotNumber);

        if (gameState == null) {
            System.err.println("[SlotSelectionController] Failed to load slot " + slotNumber);
            return;
        }

        if (onStartGame != null) {
            onStartGame.startGame(slotNumber, gameState);  // ← có gameState = Load Game
        }
    }

    public void handleDeleteSlot(int slotNumber) {
        System.out.println("[SlotSelectionController] Deleting slot " + slotNumber);
        boolean deleted = repository.deleteSlot(slotNumber);

        if (deleted) {
            slotManager.reloadSlot(slotNumber);
            System.out.println("[SlotSelectionController] Slot " + slotNumber + " deleted");
        }
    }

    public void handleBackToMenu() {
        System.out.println("[SlotSelectionController] Back to menu");
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }
}