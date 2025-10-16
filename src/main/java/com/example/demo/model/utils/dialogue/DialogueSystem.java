package com.example.demo.model.utils.dialogue;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * sử dụng DialogueLoader để đọc file -> xử lý logic kịch bản
 * -> gọi DialogueBox để hiển thị hội thoại
 */
public class DialogueSystem {
    private DialogueLoader loader;      // đọc file kịch bản.
    private DialogueBox dialogueBox;    // hiển thị hội thoại

    // Một hàng đợi (Queue) để lưu trữ các dòng lệnh đã xử lý
    private Queue<String> pendingLines;

    public DialogueSystem(String initialDialogueFile, DialogueBox dialogueBox) {
        this.dialogueBox = dialogueBox;
        this.pendingLines = new LinkedList<>();
        loadDialogue(initialDialogueFile);
    }

    /**
     * Tải và xử lý một file txt.
     */
    public void loadDialogue(String filePath) {
        System.out.println("Loading script from:  " + filePath);
        loader = new DialogueLoader(filePath);
        pendingLines.clear(); // Xóa các dòng lệnh cũ nếu có
        loadAllLines();
    }

    /**
     * xử lý luật
     */
    private void loadAllLines() {
        String line;
        StringBuilder currentCommand = new StringBuilder();

        while ((line = loader.getNextValidLine()) != null) {
            // Nếu dòng mới là một lệnh
            if (line.startsWith("-text") || line.startsWith("+background:")) {
                //  hãy lưu lệnh vào hàng đợi trước
                if (currentCommand.length() > 0) {
                    pendingLines.offer(currentCommand.toString());
                }
                currentCommand = new StringBuilder(line);
            } else {
                // Nếu vẫn thuộc lệnh cũ thì ghép lại
                if (currentCommand.length() > 0) {
                    currentCommand.append(" ").append(line);
                }
            }
        }
        // Lưu lệnh cuối sau khi hết vòng lặp
        if (currentCommand.length() > 0) {
            pendingLines.offer(currentCommand.toString());
        }
    }

    /**
     * Phân tích tát cả lệnh trong pendingLines, chuyển hết thành mảng <DialogueLine>
     * và gửi danh sách này cho DialogueBox để hiển thị
     */
    public void start() {
        if (pendingLines.isEmpty()) return;

        List<DialogueBox.DialogueLine> allLines = new ArrayList<>();

        while (!pendingLines.isEmpty()) {
            String cmd = pendingLines.poll();

            if (cmd.startsWith("-text")) {
                try {
                    // Tách chuỗi lệnh để lấy ra tên nhân vật và câu thoại
                    // Ví dụ: "-text EGG: Hello" -> content = "EGG: Hello"
                    String content = cmd.substring(6).trim();
                    int targetIndex = content.indexOf(":");
                    String character = content.substring(0, targetIndex).trim().toUpperCase();
                    String sentence = content.substring(targetIndex + 1).trim();

                    // đổi tên nhân vật từ String thành enum Speaker.
                    DialogueBox.DialogueLine.Speaker speaker = DialogueBox.DialogueLine.getByName(character);

                    // Thêm DialogueLine vào danh sách
                    allLines.add(new DialogueBox.DialogueLine(speaker, sentence));

                } catch (Exception e) {
                    System.err.println("Erro when loading line: " + cmd);
                    e.printStackTrace();
                }
            }
            // TODO: else if (cmd.startsWith("+background:"))
        }

        // gửi danh sách câu thoại cho DialogueBox
        if (!allLines.isEmpty()) {
            dialogueBox.start(allLines.toArray(new DialogueBox.DialogueLine[0]));
        }
    }

    public boolean isComplete() {
        return !dialogueBox.isActive();
    }
}