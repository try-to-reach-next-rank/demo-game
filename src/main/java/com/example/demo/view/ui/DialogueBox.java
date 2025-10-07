package com.example.demo.view.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DialogueBox extends UIComponent {
    private String[] lines;
    private int currentLine = 0;

    // typewriter state
    private String displayedText = "";
    private int charIndex = 0;
    private double timePerChar = 0;
    private boolean lineComplete = false;
    private final Font font = new Font("Verdana", 20);

    public void start(String[] lines) {
        this.lines = lines;
        this.currentLine = 0;
        prepareLine();
        show();
    }

    private void prepareLine() {
        displayedText = "";
        charIndex = 0;
        timePerChar = 0;
        lineComplete = false;
    }

    @Override
    public void update(double deltaTime) {
        if (!active || lines == null || currentLine >= lines.length) return;
        if (lineComplete) return;

        timePerChar += deltaTime;
        double typeSpeed = 0.03; // seconds per character
        while (timePerChar >= typeSpeed && charIndex < lines[currentLine].length()) {
            displayedText += lines[currentLine].charAt(charIndex++);
            timePerChar -= typeSpeed;
        }

        if (charIndex >= lines[currentLine].length()) {
            lineComplete = true;
        }
    }

    @Override
    public void render(GraphicsContext gc, double width, double height) {
        if (!active || lines == null || currentLine >= lines.length) return;

        double boxHeight = 150;
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(20, height - boxHeight - 20, width - 40, boxHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(font);

        double x = 40;
        double y = height - boxHeight + 40;
        gc.fillText(displayedText, x, y);
    }

    @Override
    public void handleInput(KeyCode code) {
        if (!active) return;

        if (code == KeyCode.SPACE || code == KeyCode.ENTER) {
            if (!lineComplete) {
                // instantly show full line
                displayedText = lines[currentLine];
                charIndex = lines[currentLine].length();
                lineComplete = true;
            } else {
                // move to next line
                currentLine++;
                if (currentLine >= lines.length) {
                    hide();
                } else {
                    prepareLine();
                }
            }
        }
    }
}
