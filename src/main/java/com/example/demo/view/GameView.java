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

import com.example.demo.view.EndGameVideoView;
import javafx.scene.paint.Color;

public class GameView extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameWorld world ;
    private final GameController controller;
    private final UIView uiView;
    private final CoreView coreView;

    private TransitionEffect transitionEffect;


    private final EndGameVideoView endGameVideoView;
    private final Pane blackScreen;

    public GameView(GameWorld world, GameController controller) {
        this.world = world;
        this.controller = controller;

        this.canvas = new Canvas(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        this.gc = canvas.getGraphicsContext2D();

        this.uiView = new UIView(controller); // if you have UI overlay
        this.coreView = new CoreView(gc, world);
        transitionEffect = new TransitionEffect(GameVar.TRANSITION_DURATION);


        this.endGameVideoView = new EndGameVideoView();
        this.endGameVideoView.setVisible(false); // Ẩn ban đầu


        this.blackScreen = new Pane();
        this.blackScreen.setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        this.blackScreen.setStyle("-fx-background-color: black;");
        this.blackScreen.setVisible(false); // Ẩn ban đầu


        this.endGameVideoView.setOnVideoFinished(this::showBlackScreen);

        getChildren().addAll(canvas, uiView.getRoot(), endGameVideoView, blackScreen);
    }

    public void render() {
        gc.clearRect(0, 0, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        coreView.render(gc);
        uiView.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        if (transitionEffect != null && transitionEffect.isActive()     ) {

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
            default:
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



    /**
     * Bắt đầu chuỗi sự kiện kết thúc game.
     * Ẩn game và UI, sau đó phát video.
     */
    public void showEndGameSequence() {

        canvas.setVisible(false);
        uiView.getRoot().setVisible(false);


        endGameVideoView.setVisible(true);
        endGameVideoView.toFront();
        endGameVideoView.playVideo();
    }

    /**
     * Được gọi khi video kết thúc.
     * Ẩn video và hiển thị màn hình đen.
     */
    private void showBlackScreen() {

        endGameVideoView.setVisible(false);


        blackScreen.setVisible(true);
        blackScreen.toFront();

    }
}