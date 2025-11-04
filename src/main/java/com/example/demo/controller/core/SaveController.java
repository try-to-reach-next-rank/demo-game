package com.example.demo.controller.core;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.state.GameState;
import com.example.demo.repository.SaveDataRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * sử dụng thư viện Gson của Google để chuyển đổi đối tượng Java sang Json và ngược lại.
 */
public class SaveController {
    // một đối tượng Gson duy nhất được tạo ra cho cả chương trình
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger log = LoggerFactory.getLogger(SaveController.class);
    private final SaveDataRepository repository;

    public SaveController() {
        this.repository = new SaveDataRepository();
    }

    /**
     *  Lưu một đối tượng Java bất kỳ vào một file dưới định dạng chuỗi JSON.
     *  Lưu ý: có thể bị ghi đè
     * @param data      Đối tượng cần lưu. Đây có thể là bất kỳ đối tượng nào (ví dụ: PlayerData, GameSettings,...).
     * @param fileName  Tên của file (hoặc đường dẫn đầy đủ) để lưu dữ liệu. Ví dụ: "savegame.json".
     */
    public static void save(Object data, String fileName){
        try{
            Path path = Paths.get(fileName);
            String jsonString = gson.toJson(data);

            Files.writeString(path, jsonString);

            SaveController.log.info("Successfully saved data to {}", fileName);
        } catch (Exception e) {
            System.err.println("Error: Failed to save data to " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Đọc dữ liệu từ một file JSON và chuyển đổi nó trở lại thành một đối tượng Java.
     * Đây là một phương thức generic (kiểu dữ liệu chung), có thể tải bất kỳ loại đối tượng nào
     * mà không cần phải viết một hàm riêng cho mỗi loại.
     *
     * @param <T>       Kiểu dữ liệu của đối tượng mà chúng ta muốn tải về (ví dụ: PlayerData).
     * @param fileName  Tên của file JSON cần đọc.
     * @param classType Lớp (Class) của đối tượng cần tải, ví dụ: `PlayerData.class`.
     *                  Gson cần thông tin này để biết cách tạo lại đối tượng từ dữ liệu JSON.
     * @return Trả về một đối tượng của kiểu <T> nếu tải thành công.
     *         Trả về `null` nếu file không tồn tại hoặc có bất kỳ lỗi nào xảy ra trong quá trình đọc/phân tích file.
     */
    public static <T> T load(String fileName, Class<T> classType) {
        try {
            Path path = Paths.get(fileName);

            if (!Files.exists(path)) {
                log.info("Save file not found: {}. Returning null.", fileName);
                return null;
            }

            String jsonString = Files.readString(path);

            T loadedObject = gson.fromJson(jsonString, classType);

            log.info("Successfully loaded data from {}", fileName);
            return loadedObject;
        } catch (Exception e) {
            System.err.println("Error: Failed to load data from " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    public void saveGame(GameWorld world, int slotNumber) {
        log.info("Saving game to slot {}...", slotNumber);
        GameState state = new GameState(world);
        repository.saveSlot(slotNumber, state);
        log.info("Save complete.");
    }

    public GameState loadGame(int slotNumber) {
        log.info("Loading from slot {}...", slotNumber);
        GameState loaded = repository.loadSlot(slotNumber);

        if (loaded != null) {
            log.info("Loaded successfully!");
        } else {
            log.warn("No valid save found.");
        }

        return loaded;
    }

    /**
     * Check if slot exists
     */
    public boolean slotExists(int slotNumber) {
        return repository.slotExists(slotNumber);
    }

    /**
     * Delete save slot
     */
    public boolean deleteSlot(int slotNumber) {
        return repository.deleteSlot(slotNumber);
    }
}
