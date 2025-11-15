package com.example.demo.model.core.effects;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ScorePopupEffect extends VisualEffect {

    private String textToShow;
    private double initialY;
    private double speed = -50.0;
    private Font font = Font.font("Verdana", FontWeight.BOLD, 22);

    public ScorePopupEffect() {
        super("scorePopup");
    }

    @Override
    protected void onActivate() {
        super.onActivate();
        this.initialY = this.y;
    }

    @Override
    public void customize(Object... params) {
        if (params.length > 0 && params[0] instanceof String) {
            this.textToShow = (String) params[0];
        } else {
            this.textToShow = "ERR"; // Mặc định nếu tham số sai
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (!isActive()) return;
        this.y = initialY + speed * timer.getElapsed();
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive() || textToShow == null) return;

        double progress = timer.getElapsed()/ durationSeconds;
        double alpha = 1.0 - progress;
        if (alpha < 0) alpha = 0;

        gc.setGlobalAlpha(alpha);
        gc.setFont(font);
        gc.setFill(Color.GOLD);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.strokeText(textToShow, x, y);
        gc.fillText(textToShow, x, y);

        gc.setGlobalAlpha(1.0);
    }

    @Override
    public VisualEffect clone() {
        return new ScorePopupEffect();
    }
}