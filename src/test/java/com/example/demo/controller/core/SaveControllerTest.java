package com.example.demo.controller.core;

import com.google.gson.annotations.Expose;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class SaveControllerTest {

    private static final String TEST_FILE = "test_save.json";

    public static class PlayerData {
        public String name;
        public int score;
        transient boolean tempFlag; // không lưu

        PlayerData(String name, int score, boolean tempFlag) {
            this.name = name;
            this.score = score;
            this.tempFlag = tempFlag;
        }

        PlayerData() {}
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void testSaveCreatesFile() throws IOException {
        PlayerData data = new PlayerData("Alice", 100, true);
        SaveController.save(data, TEST_FILE);
        assertTrue(Files.exists(Paths.get(TEST_FILE)));
    }

    @Test
    void testSaveAndLoadObjectIntegrity() {
        PlayerData data = new PlayerData("Bob", 200, true);
        SaveController.save(data, TEST_FILE);

        PlayerData loaded = SaveController.load(TEST_FILE, PlayerData.class);
        assertNotNull(loaded);
        assertEquals("Bob", loaded.name);
        assertEquals(200, loaded.score);
        assertFalse(loaded.tempFlag); // transient không được lưu
    }

    @Test
    void testLoadNonExistingFileReturnsNull() {
        PlayerData loaded = SaveController.load("non_existing.json", PlayerData.class);
        assertNull(loaded);
    }

    @Test
    void testSaveHandlesInvalidPathGracefully() {
        assertDoesNotThrow(() ->
                SaveController.save(new PlayerData("Err", 1, false), "/invalid/path/save.json"));
    }

    @Test
    void testLoadInvalidJsonReturnsNull() throws IOException {
        Files.writeString(Paths.get(TEST_FILE), "{invalid json}");
        PlayerData loaded = SaveController.load(TEST_FILE, PlayerData.class);
        assertNull(loaded);
    }
}
