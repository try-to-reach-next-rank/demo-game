package com.example.demo.view;

import com.example.demo.controller.core.GameController;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.effects.TransitionEffect;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class GameView extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameWorld world;
    private final GameController controller;
    private final UIView uiView;
    private final CoreView coreView;

    private TransitionEffect transitionEffect;

    public GameView(GameWorld world, GameController controller) {
        this.world = world;
        this.controller = controller;

        this.canvas = new Canvas(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        this.uiView = new UIView(controller); // if you have UI overlay
        this.coreView = new CoreView(gc, world);
        transitionEffect = new TransitionEffect(GameVar.TRANSITION_DURATION);
        getChildren().add(uiView.getRoot());
    }

    public void render() {
        gc.clearRect(0, 0, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        coreView.render(gc);
        uiView.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        if (transitionEffect != null && transitionEffect.isActive()) {
            transitionEffect.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        }
    }

    public void handleKeyPressed(KeyEvent e) {
        uiView.handleInput(e.getCode());

        if (!uiView.hasActiveUI()) {
            controller.onKeyPressed(e.getCode());
        }

        switch (e.getCode()) {
            case F:
                coreView.toggleFlip();
                break;
        }

        e.consume();
    }

    public void handleKeyReleased(KeyEvent e) {
        controller.onKeyReleased(e.getCode());
    }

    public UIView getUiView() {
        return uiView;
    }

    public CoreView getCoreView() {
        return coreView;
    }

    public void reset() {
        coreView.reset();
    }

    public void update(double deltaTime) {
        coreView.update(deltaTime);
        if (transitionEffect != null && transitionEffect.isActive()) {
            transitionEffect.update(deltaTime);
        }
        uiView.update(deltaTime);
    }

    public void startTransition(Runnable onMidpoint, Runnable onEnd) {
        transitionEffect.start(onMidpoint, onEnd);
    }
}
