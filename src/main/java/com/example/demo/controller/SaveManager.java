package com.example.demo.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * sử dụng thư viện Gson của Google để chuyển đổi đối tượng Java sang Json và ngược lại.
 */
public class SaveManager {
    // một đối tượng Gson duy nhất được tạo ra cho cả chương trình
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

            System.out.println("Successfully saved data to " + fileName);
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
                System.out.println("Save file not found: " + fileName + ". Returning null.");
                return null;
            }

            String jsonString = Files.readString(path);

            T loadedObject = gson.fromJson(jsonString, classType);

            System.out.println("Successfully loaded data from " + fileName);
            return loadedObject;
        } catch (Exception e) {
            System.err.println("Error: Failed to load data from " + fileName);
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Ví dụ cách dùng
     * public class PlayerData {
     *     // Các biến thành viên (fields)
     *     private String playerName;
     *     private int score;
     *     private int level;
     *     private static final int MAX_LEVEL = 99; // Biến static
     *     private transient boolean isInvincible;  // Biến transient (tạm thời)
     *
     *     // Constructor, getters, setters...
     *
     *     public void addScore(int points) { // Đây là một phương thức (method)
     *         this.score += points;
     *     }
     * }
     * PlayerData player1 = new PlayerData();
     * player1.setPlayerName("Phuc");
     * player1.setScore(1000);
     * player1.setLevel(5);
     * player1.setInvincible(true); // Gán giá trị cho biến transient
     *
     * {
     *   "playerName": "Phuc",
     *   "score": 1000,
     *   "level": 5
     * }
     *
     * các thứ ko được lưu, static, methods, transient( bảo với gson đừng lưu biến này)
     */
}
