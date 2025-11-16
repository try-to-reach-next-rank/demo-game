package com.example.demo.model.core.effects;

import com.example.demo.model.assets.AssetManager;
import com.example.demo.utils.var.GameVar;

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
    private Font font;

    public ScorePopupEffect(String effectKey) {
        super(effectKey);
        this.font = AssetManager.getInstance().getFont("Verdana-Bold", GameVar.SCORE_FONT_SIZE);
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
            this.textToShow = "NULL"; // Mặc định nếu tham số sai
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
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
        return new ScorePopupEffect(this.effectKey);
    }
}