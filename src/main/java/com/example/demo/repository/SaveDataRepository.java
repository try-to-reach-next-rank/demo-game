package com.example.demo.repository;

import com.example.demo.controller.SaveManager;
import com.example.demo.model.state.GameState;
import com.example.demo.model.state.BrickData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class SaveDataRepository {
    private static final String SAVE_DIR = "src\\main\\resources\\Saves\\";

    public SaveDataRepository() {
        createSaveDirectoryIfNotExists();
    }

    private void createSaveDirectoryIfNotExists() {
        try {
            Path savePath = Paths.get(SAVE_DIR);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
                System.out.println("[SaveDataRepository] Created save directory: " + SAVE_DIR);
            }
        } catch (IOException e) {
            System.err.println("[SaveDataRepository] Failed to create save directory!");
            e.printStackTrace();
        }
    }

    private String getSlotPath(int slotNumber) {
        return SAVE_DIR + "slot_" + slotNumber + ".json";
    }

    public boolean slotExists(int slotNumber) {
        Path path = Paths.get(getSlotPath(slotNumber));
        return Files.exists(path);
    }

    public GameState loadSlot(int slotNumber) {
        if (!slotExists(slotNumber)) {
            System.out.println("[SaveDataRepository] Slot " + slotNumber + " does not exist.");
            return null;
        }

        String path = getSlotPath(slotNumber);
        GameState state = SaveManager.load(path, GameState.class);

        if (state != null) {
            System.out.println("[SaveDataRepository] Loaded slot " + slotNumber + " successfully.");
        }

        return state;
    }

    public List<BrickData> loadBricksDataOnly(int slotNumber) {
        if (!slotExists(slotNumber)) {
            return null;
        }

        GameState state = loadSlot(slotNumber);
        return state != null ? state.getBricksData() : null;
    }

    public void saveSlot(int slotNumber, GameState state) {
        if (state == null) {
            System.err.println("[SaveDataRepository] Cannot save null GameState!");
            return;
        }

        String path = getSlotPath(slotNumber);
        SaveManager.save(state, path);

        System.out.println("[SaveDataRepository] Saved slot " + slotNumber);
    }

    public boolean deleteSlot(int slotNumber) {
        try {
            Path path = Paths.get(getSlotPath(slotNumber));
            boolean deleted = Files.deleteIfExists(path);

            if (deleted) {
                System.out.println("[SaveDataRepository] Deleted slot " + slotNumber);
            }

            return deleted;
        } catch (IOException e) {
            System.err.println("[SaveDataRepository] Failed to delete slot " + slotNumber);
            e.printStackTrace();
            return false;
        }
    }

    public LocalDateTime getLastModified(int slotNumber) {
        if (!slotExists(slotNumber)) {
            return null;
        }

        try {
            Path path = Paths.get(getSlotPath(slotNumber));
            FileTime fileTime = Files.getLastModifiedTime(path);
            return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            System.err.println("[SaveDataRepository] Failed to get last modified time for slot " + slotNumber);
            e.printStackTrace();
            return null;
        }
    }
}