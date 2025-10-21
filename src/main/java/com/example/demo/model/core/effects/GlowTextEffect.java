package com.example.demo.model.core.effects;

import com.example.demo.model.core.VisualEffect;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
        title.setFont(Font.font(48));

        offset = new SimpleDoubleProperty(0);

        // Bind gradient to offset
        title.fillProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double start = offset.get();
                    return new LinearGradient(
                            start, 0, start + 1.0, 0, true, CycleMethod.REPEAT,
                            new Stop(0.0, Color.web("#555555")),
                            new Stop(0.30, Color.web("#555555")),
                            new Stop(0.40, Color.web("#00b8ff")),
                            new Stop(0.50, Color.web("#ffffff")),
                            new Stop(0.60, Color.web("#00b8ff")),
                            new Stop(0.70, Color.web("#555555")),
                            new Stop(1.0, Color.web("#555555"))
                    );
                }, offset)
        );

        // Setup animation
        shimmer = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(offset, 0)),
                new KeyFrame(Duration.seconds(2.5), new KeyValue(offset, 1))
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
