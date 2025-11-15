package com.example.demo.model.core.effects;

import com.example.demo.utils.var.GameVar;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GlowTextEffect extends VisualEffect {
    private final Text title;
    private final DoubleProperty offset;
    private final Timeline shimmer;

    public GlowTextEffect() {
        super();

        title = new Text("Arkanoid");
        title.setFont(Font.font(GameVar.GLOW_FONT_SIZE));

        offset = new SimpleDoubleProperty(GameVar.GLOW_OFFSET_START);

        // Bind gradient to offset
        title.fillProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double start = offset.get();
                    return new LinearGradient(
                            start, 0, start + GameVar.GLOW_OFFSET_END, 0, true, CycleMethod.REPEAT,
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[0], GameVar.GLOW_COLOR_BASE),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[1], GameVar.GLOW_COLOR_BASE),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[2], GameVar.GLOW_COLOR_CYAN),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[3], GameVar.GLOW_COLOR_HIGHLIGHT),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[4], GameVar.GLOW_COLOR_CYAN),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[5], GameVar.GLOW_COLOR_BASE),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[6], GameVar.GLOW_COLOR_BASE)
                    );
                }, offset)
        );

        // Setup animation
        shimmer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(offset, GameVar.GLOW_OFFSET_START)),
                new KeyFrame(Duration.seconds(GameVar.GLOW_ANIMATION_DURATION), new KeyValue(offset, GameVar.GLOW_OFFSET_END))
        );
        shimmer.setCycleCount(Animation.INDEFINITE);
    }

    public GlowTextEffect(Text title, Font font) {
        super();

        this.title = title;
        title.setFont(font);

        offset = new SimpleDoubleProperty(GameVar.GLOW_OFFSET_START);

        // Bind gradient to offset
        title.fillProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double start = offset.get();
                    return new LinearGradient(
                            start, 0, start + GameVar.GLOW_OFFSET_END, 0,
                            true, CycleMethod.REPEAT,
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[0], GameVar.GLOW_COLOR_BASE),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[1], GameVar.GLOW_COLOR_BASE),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[2], GameVar.GLOW_COLOR_CYAN),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[3], GameVar.GLOW_COLOR_HIGHLIGHT),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[4], GameVar.GLOW_COLOR_CYAN),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[5], GameVar.GLOW_COLOR_BASE),
                            new Stop(GameVar.GLOW_GRADIENT_STOPS[6], GameVar.GLOW_COLOR_BASE)
                    );
                }, offset)
        );

        // Setup animation
        shimmer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(offset, GameVar.GLOW_OFFSET_START)),
                new KeyFrame(Duration.seconds(GameVar.GLOW_ANIMATION_DURATION), new KeyValue(offset, GameVar.GLOW_OFFSET_END))
        );
        shimmer.setCycleCount(Animation.INDEFINITE);
    }

    @Override
    protected void onActivate() {
        shimmer.play();
    }

    @Override
    protected void onDeactivate() {
        shimmer.stop();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        // Shimmer runs via JavaFX Timeline, so no manual offset update needed
    }

    public Text getNode() {
        return title;
    }


    @Override
        public void render(GraphicsContext gc) {
        if (!active) return;

        gc.save();
        gc.translate(x, y);

        // Fill text manually, since we’re drawing on a Canvas
        gc.setFont(title.getFont());
        gc.setFill(title.getFill());
        gc.fillText(title.getText(), 0, 0);
        gc.restore();
    }

    @Override
    public VisualEffect clone() {
        return new GlowTextEffect();
    }
}
