package com.example.demo.repository;

import com.example.demo.controller.core.SaveController;
import com.example.demo.model.state.GameState;
import com.example.demo.model.state.BrickData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(SaveDataRepository.class);

    public SaveDataRepository() {
        createSaveDirectoryIfNotExists();
    }

    private void createSaveDirectoryIfNotExists() {
        try {
            Path savePath = Paths.get(SAVE_DIR);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
                SaveDataRepository.log.info("[SaveDataRepository] Created save directory: {}", SAVE_DIR);
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
            log.info("Slot {} does not exist.", slotNumber);
            return null;
        }

        String path = getSlotPath(slotNumber);
        GameState state = SaveController.load(path, GameState.class);

        if (state != null) {
            log.info("Loaded slot {} successfully.", slotNumber);
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
        SaveController.save(state, path);

        log.info("Saved slot {}", slotNumber);
    }

    public boolean deleteSlot(int slotNumber) {
        try {
            Path path = Paths.get(getSlotPath(slotNumber));
            boolean deleted = Files.deleteIfExists(path);

            if (deleted) {
                log.info("[SaveDataRepository] Deleted slot {}", slotNumber);
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