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
    private static final Path SAVE_DIR = Paths.get("src", "main", "resources", "Saves");
    private static final Logger log = LoggerFactory.getLogger(SaveDataRepository.class);

    public SaveDataRepository() {
        createSaveDirectoryIfNotExists();
    }

    private void createSaveDirectoryIfNotExists() {
        try {
            if (!Files.exists(SAVE_DIR)) {
                Files.createDirectories(SAVE_DIR);
                log.info("[SaveDataRepository] Created save directory: {}", SAVE_DIR.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("[SaveDataRepository] Failed to create save directory!");
            e.printStackTrace();
        }
    }

    private Path getSlotPath(int slotNumber) {
        return SAVE_DIR.resolve("slot_" + slotNumber + ".json");
    }

    public boolean slotExists(int slotNumber) {
        return Files.exists(getSlotPath(slotNumber));
    }

    public GameState loadSlot(int slotNumber) {
        if (!slotExists(slotNumber)) {
            log.warn("Slot {} does not exist.", slotNumber);
            return null;
        }

        Path path = getSlotPath(slotNumber);
        GameState state = SaveController.load(path.toString(), GameState.class);

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
            log.error("[SaveDataRepository] Cannot save null GameState!");
            return;
        }

        Path path = getSlotPath(slotNumber);
        SaveController.save(state, path.toString());

        log.info("Saved slot {}", slotNumber);
    }

    public boolean deleteSlot(int slotNumber) {
        try {
            Path path = getSlotPath(slotNumber);
            boolean deleted = Files.deleteIfExists(path);

            if (deleted) {
                log.info("[SaveDataRepository] Deleted slot {}", slotNumber);
            }

            return deleted;
        } catch (IOException e) {
            log.error("[SaveDataRepository] Failed to delete slot " + slotNumber);
            e.printStackTrace();
            return false;
        }
    }
}