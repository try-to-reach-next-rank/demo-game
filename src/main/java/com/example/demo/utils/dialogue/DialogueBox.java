package com.example.demo.utils.dialogue;

import com.example.demo.view.ui.UIComponent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.example.demo.model.assets.AssetManager;
import com.example.demo.utils.Sound;
import com.example.demo.utils.var.AssetPaths;
import com.example.demo.utils.var.UtilVar;

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
    private final Font font;

    private double eggY = 0, ballY = 0, timer = 0;

    public DialogueBox() {
        var am = AssetManager.getInstance();
        var eggImg = am.getImage(AssetPaths.EGG);
        var ballImg = am.getImage(AssetPaths.BALL);

        if (eggImg != null) eggImage = eggImg;
        else {
            var eggUrl = getClass().getResource(AssetPaths.EGG);
            if (eggUrl != null) eggImage = new Image(eggUrl.toExternalForm(), UtilVar.CHARACTER_IMG_SIZE, UtilVar.CHARACTER_IMG_SIZE, true, true);
        }

        if (ballImg != null) ballImage = ballImg;
        else {
            var ballUrl = getClass().getResource(AssetPaths.BALL);
            if (ballUrl != null) ballImage = new Image(ballUrl.toExternalForm(), UtilVar.CHARACTER_IMG_SIZE, UtilVar.CHARACTER_IMG_SIZE, true, true);
        }

        this.font = am.getFont("Verdana", UtilVar.FONT_SIZE);
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

    public void resumeDialogue() {
        if (active && !lineComplete) {
            // Resume sound nếu câu chưa hoàn thành
            Sound.getInstance().loopSound("dialogue-sound");
        }
    }

    @Override
    public void update(double deltaTime) {
        if (!active || lines == null || currentLine >= lines.length) return;

        DialogueLine line = lines[currentLine];
        timePerChar += deltaTime;

        // typewriter effect
        if (!lineComplete) {
            while (timePerChar >= UtilVar.TYPE_SPEED && charIndex < line.text.length()) {
                displayedText += line.text.charAt(charIndex++);
                timePerChar -= UtilVar.TYPE_SPEED;
            }
            if (charIndex >= line.text.length()) {
                lineComplete = true;
                Sound.getInstance().stopSound("dialogue-sound");
            }
        }

        // simple bounce animation for active speaker
        timer += deltaTime * UtilVar.BOUNCE_FREQUENCY;
        double bob = Math.sin(timer * Math.PI * 3)
                * UtilVar.BOUNCE_AMPLITUDE
                * Math.exp(-timer * UtilVar.BOUNCE_DECAY);
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

        double boxH = UtilVar.BOX_HEIGHT;
        double margin = UtilVar.BOX_MARGIN;
        DialogueLine line = lines[currentLine];

        // box
        gc.setFill(Color.rgb(0, 0, 0, UtilVar.BOX_OPACITY));
        gc.fillRect(margin, height - boxH - margin, width - margin * 2, boxH);

        // characters
        if (eggImage != null) {
            gc.setGlobalAlpha(line.speaker == DialogueLine.Speaker.EGG ? 1.0 : 0.4);
            gc.drawImage(eggImage, width - UtilVar.EGG_OFFSET_X, height - boxH - UtilVar.EGG_OFFSET_Y + eggY);
        }
        if (ballImage != null) {
            gc.setGlobalAlpha(line.speaker == DialogueLine.Speaker.BALL ? 1.0 : 0.4);
            gc.drawImage(ballImage, UtilVar.BALL_OFFSET_X, height - boxH - UtilVar.BALL_OFFSET_Y + ballY);
        }
        gc.setGlobalAlpha(1.0);

        // text (simple and inside the box)
        gc.setFill(Color.WHITE);
        gc.setFont(font);

        gc.setFill(Color.WHITE);
        gc.setFont(font);

        double textMaxWidth = width - margin * 2 - UtilVar.TEXT_SIDE_GAP;
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
