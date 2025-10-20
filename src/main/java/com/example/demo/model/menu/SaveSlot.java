package com.example.demo.model.menu;

import com.example.demo.model.state.BrickData;
import java.util.List;

/**
 * Đại diện cho 1 save slot trong game
 * Design: Minimalist - chỉ chứa data cần thiết để render mini map preview
 */
public class SaveSlot {
    private int slotNumber;
    private boolean isEmpty;
    private List<BrickData> bricksData;

    /**
     * Constructor cho empty slot
     */
    public SaveSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.isEmpty = true;
        this.bricksData = null;
    }

    /**
     * Constructor cho occupied slot
     */
    public SaveSlot(int slotNumber, List<BrickData> bricksData) {
        this.slotNumber = slotNumber;
        this.isEmpty = false;
        this.bricksData = bricksData;
    }

    // Getters
    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public List<BrickData> getBricksData() {
        return bricksData;
    }
}