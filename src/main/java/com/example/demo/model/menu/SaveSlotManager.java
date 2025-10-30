package com.example.demo.model.menu;

import com.example.demo.repository.SaveDataRepository;
import com.example.demo.model.state.BrickData;
import com.example.demo.controller.core.SaveManager;
import java.util.List;
import java.util.Arrays;


/**
 * Quản lý tất cả save slots
 * Responsibilities:
 * - Load/reload slots
 * - Provide access to slot data
 * - Coordinate với Repository
 */
public class SaveSlotManager {
    private final SaveDataRepository repository;
    private SaveSlot slot1;
    private SaveSlot slot2;

    public SaveSlotManager() {
        this.repository = new SaveDataRepository();
        this.slot1 = null;
        this.slot2 = null;
    }

    /**
     * Load cả 2 slots
     */
    public void loadAllSlots() {
        System.out.println("[SaveSlotManager] Loading all slots...");
        slot1 = loadSlot(1);
        slot2 = loadSlot(2);
        System.out.println("[SaveSlotManager] Slot 1: " + slot1);
        System.out.println("[SaveSlotManager] Slot 2: " + slot2);
    }

    /**
     * Load 1 slot
     */
    private SaveSlot loadSlot(int slotNumber) {
        if (!repository.slotExists(slotNumber)) {
            return new SaveSlot(slotNumber);
        }

        List<BrickData> bricksData = repository.loadBricksDataOnly(slotNumber);

        if (bricksData == null) {
            System.err.println("[SaveSlotManager] Failed to load slot " + slotNumber);
            return new SaveSlot(slotNumber);
        }

        return new SaveSlot(slotNumber, bricksData);
    }

    /**
     * Lấy slot theo number
     */
    public SaveSlot getSlot(int slotNumber) {
        if (slotNumber == 1) {
            return slot1;
        } else if (slotNumber == 2) {
            return slot2;
        } else {
            throw new IllegalArgumentException("Invalid slot number: " + slotNumber);
        }
    }

    /**
     * Lấy tất cả slots
     */
    public List<SaveSlot> getAllSlots() {
        return Arrays.asList(slot1, slot2);
    }

    /**
     * Reload 1 slot
     */
    public void reloadSlot(int slotNumber) {
        System.out.println("[SaveSlotManager] Reloading slot " + slotNumber + "...");

        if (slotNumber == 1) {
            slot1 = loadSlot(1);
            System.out.println("[SaveSlotManager] Slot 1 reloaded: " + slot1);
        } else if (slotNumber == 2) {
            slot2 = loadSlot(2);
            System.out.println("[SaveSlotManager] Slot 2 reloaded: " + slot2);
        } else {
            throw new IllegalArgumentException("Invalid slot number: " + slotNumber);
        }
    }

    /**
     * Check slot empty
     */
    public boolean isSlotEmpty(int slotNumber) {
        SaveSlot slot = getSlot(slotNumber);
        return slot != null && slot.isEmpty();
    }

    /**
     * Lấy repository (cho Controller)
     */
    public SaveDataRepository getRepository() {
        return repository;
    }

    public void reloadAll() {
        loadAllSlots();
    }

}