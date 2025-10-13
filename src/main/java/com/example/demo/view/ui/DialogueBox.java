package com.example.demo.view.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import com.example.demo.model.utils.Sound;
import javafx.scene.text.Text;

public class DialogueBox extends UIComponent {

    public static class DialogueLine {
        public enum Speaker { EGG, BALL }
        public final Speaker speaker;
        public final String text;

        public DialogueLine(Speaker speaker, String text) {
            this.speaker = speaker;
            this.text = text;
        }

        public static Speaker getByName(String name) {
            for (Speaker speaker : Speaker.values()) {
                if (speaker.name().equalsIgnoreCase(name)) {
                    return speaker;
                }
            }
            // If the loop finishes, the name was invalid. Throw an error.
            throw new IllegalArgumentException("No enum constant for speaker " + name);
        }
    }

    private DialogueLine[] lines;
    private int currentLine = 0;
    private String displayedText = "";
    private int charIndex = 0;
    private boolean lineComplete = false;
    private double timePerChar = 0;

    private Image eggImage, ballImage;
    private final Font font = new Font("Verdana", 20);

    private double eggY = 0, ballY = 0, timer = 0;

    public DialogueBox() {
        var eggUrl = getClass().getResource("/images/egg.png");
        var ballUrl = getClass().getResource("/images/Ball.png");

        if (eggUrl != null) eggImage = new Image(eggUrl.toExternalForm(), 130, 130, true, true);
        if (ballUrl != null) ballImage = new Image(ballUrl.toExternalForm(), 130, 130, true, true);
    }

    public void start(DialogueLine[] lines) {
        this.lines = lines;
        this.currentLine = 0;
        show();
        prepareLine();
    }

    private void prepareLine() {
        displayedText = "";
        charIndex = 0;
        lineComplete = false;
        timePerChar = 0;
        Sound.getInstance().loopSound("dialogue-sound");
    }

    @Override
    public void update(double deltaTime) {
        if (!active || lines == null || currentLine >= lines.length) return;

        DialogueLine line = lines[currentLine];
        timePerChar += deltaTime;

        // typewriter effect
        double typeSpeed = 0.03;
        if (!lineComplete) {
            while (timePerChar >= typeSpeed && charIndex < line.text.length()) {
                displayedText += line.text.charAt(charIndex++);
                timePerChar -= typeSpeed;
            }
            if (charIndex >= line.text.length()) {
                lineComplete = true;
                Sound.getInstance().stopSound("dialogue-sound");
            }
        }

        // simple bounce animation for active speaker
        timer += deltaTime * 5;
        double bob = Math.sin(timer * Math.PI * 3) * 12 * Math.exp(-timer * 1.5);
        if (line.speaker == DialogueLine.Speaker.EGG) {
            eggY = !lineComplete ? bob : 0;
            ballY = 0;
        } else {
            ballY = !lineComplete ? bob : 0;
            eggY = 0;
        }
    }

    @Override
    public void render(GraphicsContext gc, double width, double height) {
        if (!active || lines == null || currentLine >= lines.length) return;

        double boxH = 160;
        double margin = 40;
        DialogueLine line = lines[currentLine];

        // box
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(margin, height - boxH - margin, width - margin * 2, boxH);

        // characters
        if (eggImage != null) {
            gc.setGlobalAlpha(line.speaker == DialogueLine.Speaker.EGG ? 1.0 : 0.4);
            gc.drawImage(eggImage, width - 180, height - boxH - 140 + eggY);
        }
        if (ballImage != null) {
            gc.setGlobalAlpha(line.speaker == DialogueLine.Speaker.BALL ? 1.0 : 0.4);
            gc.drawImage(ballImage, 50, height - boxH - 160 + ballY);
        }
        gc.setGlobalAlpha(1.0);

        // text (simple and inside the box)
        gc.setFill(Color.WHITE);
        gc.setFont(font);

        gc.setFill(Color.WHITE);
        gc.setFont(font);

// Text box padding
        double textPadding = 20;
        double textMaxWidth = width - margin * 2 - 200; // leaves room for images

        double textY = height - boxH - margin + 60;
        double textX = (line.speaker == DialogueLine.Speaker.BALL)
                ? margin + 160
                : width - margin - textMaxWidth - 160;

// draw wrapped text
        drawWrappedText(gc, displayedText, textX, textY, textMaxWidth, 26);

    }


    @Override
    public void handleInput(KeyCode code) {
        if (!active || lines == null) return;

        if (code == KeyCode.SPACE || code == KeyCode.ENTER) {
            if (!lineComplete) {
                // instantly complete current line
                displayedText = lines[currentLine].text;
                lineComplete = true;
                Sound.getInstance().stopSound("dialogue-sound");
            } else {
                // move to next or end
                currentLine++;
                if (currentLine >= lines.length) hide();
                else prepareLine();
            }
        }
    }

    public boolean isActive() { return active; }

    private void drawWrappedText(GraphicsContext gc, String text, double x, double y, double maxWidth, double lineHeight) {
        Font font = gc.getFont();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        double curY = y;

        for (String word : words) {
            String testLine = line + word + " ";

            // Measure width using JavaFX Text node
            Text helper = new Text(testLine);
            helper.setFont(font);
            double testWidth = helper.getLayoutBounds().getWidth();

            if (testWidth > maxWidth && line.length() > 0) {
                gc.fillText(line.toString(), x, curY);
                line = new StringBuilder(word + " ");
                curY += lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }

        if (!line.isEmpty()) {
            gc.fillText(line.toString(), x, curY);
        }
    }
}
