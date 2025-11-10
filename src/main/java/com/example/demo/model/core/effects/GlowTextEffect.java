package com.example.demo.model.core.effects;

import com.example.demo.utils.var.GameVar;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GlowTextEffect extends VisualEffect {

    private String text;
    private Font font;
    private double offset = 0;

    // Default constructor (optional)
    public GlowTextEffect() {
        super();
        this.text = "Arkanoid";
        this.font = GameVar.GLOW_FONT_SIZE;
    }

    // Constructor with custom text and font
    public GlowTextEffect(String text, Font font) {
        super();
        this.text = text;
        this.font = font;
    }

    @Override
    public void update(double deltaTime) {
        if (!active) return;

        // Simple shimmer animation by moving offset
        offset += deltaTime / GameVar.GLOW_ANIMATION_DURATION;
        if (offset > 1) offset -= 1;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;

        gc.save();
        gc.translate(x, y);

        gc.setFont(font);
        gc.setFill(createGradient());
        gc.fillText(text, 0, 0);

        gc.restore();
    }

    private LinearGradient createGradient() {
        return new LinearGradient(
                offset, 0, offset + 0.2, 0, true, null,
                new Stop(0, GameVar.GLOW_COLOR_BASE),
                new Stop(0.3, GameVar.GLOW_COLOR_CYAN),
                new Stop(0.5, GameVar.GLOW_COLOR_HIGHLIGHT),
                new Stop(0.7, GameVar.GLOW_COLOR_CYAN),
                new Stop(1, GameVar.GLOW_COLOR_BASE)
        );
    }

    // Optional setters for later customization
    public void setText(String text) {
        this.text = text;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Creates a glowing, moving text using JavaFX nodes.
     *
     * <p>
     * - fillProperty(): This is the color of the text. In JavaFX,
     *   properties like fillProperty() are "observable," meaning
     *   they can automatically change when we tell them to.
     * </p>
     * <p>
     *     - bind(): We "bind" the fillProperty to something else
     *      (in this case, a gradient that moves). That means every
     *      time the gradient changes, the text color updates automatically.
     * </p>
     *
     * <p>
     *     - Bindings.createObjectBinding(): Creates a binding for
     *   properties that are objects (like LinearGradient).
     *   It watches other properties (like offset) and updates
     *   the text color whenever they change.
     * </p>
     * <p>
     * - DoubleProperty / SimpleDoubleProperty: A property that
     *   holds a number (double). SimpleDoubleProperty is just a
     *   simple implementation we can use to animate or change values.
     *   Here we use it to track where the gradient starts (offset).
     * </p>
     * @return A Text node that automatically glows and animates.
     */
    public Text createGlowTextNode() {
        Text node = new Text(this.text);
        node.setFont(this.font);

        DoubleProperty offset = new SimpleDoubleProperty(GameVar.GLOW_OFFSET_START);

        node.fillProperty().bind(Bindings.createObjectBinding(() -> {
            double start = offset.get();
            return new LinearGradient(
                    start, 0, start + GameVar.GLOW_OFFSET_END, 0,
                    true, CycleMethod.REPEAT,
                    new Stop(0, GameVar.GLOW_COLOR_BASE),
                    new Stop(0.3, GameVar.GLOW_COLOR_CYAN),
                    new Stop(0.5, GameVar.GLOW_COLOR_HIGHLIGHT),
                    new Stop(0.7, GameVar.GLOW_COLOR_CYAN),
                    new Stop(1, GameVar.GLOW_COLOR_BASE)
            );
        }, offset));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(offset, 0)),
                new KeyFrame(Duration.seconds(2.5), new KeyValue(offset, 1, Interpolator.LINEAR))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        return node;
    }

    @Override
    public GlowTextEffect clone() {
        return new GlowTextEffect();
    }
}