package com.example.demo.utils.dialogue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * đọc file kịch bản (.txt) và xử lý file, loại bỏ các dòng trống, comment
 */
public class DialogueLoader {
    private StringBuilder txtLoader;

    // đánh dấu vị trí đã đọc trong trongtxtLoader
    private int position = 0;

    public DialogueLoader(String filepath) {
        txtLoader = new StringBuilder();

        try (InputStream is = DialogueLoader.class.getResourceAsStream(filepath)) {
            if (is == null) {
                System.err.println("Couldnt find script at path: " + filepath);
                return;
            }

            // Đọc file với encoding UTF-8 để hỗ trợ tiếng việt
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                // Đọc từng dòng của file và nối vào StringBuilder.
                while ((line = reader.readLine()) != null) {
                    txtLoader.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đang đọc file kịch bản: " + filepath);
            e.printStackTrace();
        }
    }

    // Xóa cmt và whitespace để chuẩn bị cho DialogueSystem
    public String getNextValidLine() {
        // Lặp cho đến khi đọc hết txtLoader
        while (position < txtLoader.length()) {
            // Tìm vị trí ký tự xuống dòng tiếp theo.
            int endIndex = txtLoader.indexOf("\n", position);
            // Nếu không tìm thấy dòng cuối cùng thì endIndex là cuối chuỗi.
            if (endIndex == -1) {
                endIndex = txtLoader.length();
            }

            // Cắt chuõi thàn dòng.
            String line = txtLoader.substring(position, endIndex);

            // Cập nhật lại vị trí con trỏ để đọc từ dòng tiếp theo
            position = endIndex + 1;

            // Xóa Whitespace
            String trimmedLine = line.trim();

            // Bỏ qua nếu dòng trống hoặc là dòng comment (bắt đầu bằng #)
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;
            }

            return trimmedLine;
        }

        return null;
    }
}