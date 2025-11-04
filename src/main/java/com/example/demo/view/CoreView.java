package com.example.demo.view;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.*;
import com.example.demo.model.map.ParallaxLayer;
import com.example.demo.model.system.ParallaxSystem;
import com.example.demo.utils.var.AssetPaths;
import com.example.demo.utils.var.GameVar;
import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CoreView {
    private final GameWorld world;
    private final GraphicsContext gc;
    private final Set<Brick> revealedBricks = new HashSet<>();
    private int brickRevealCounter = 0;
    private int currentRevealTick = 0;
    private boolean reveal = true;
    private ParallaxSystem parallaxSystem;

    public CoreView(GraphicsContext gc, GameWorld world) {
        this.gc = gc;
        this.world = world;
    }
    public void render(GraphicsContext gc) {
        if (parallaxSystem != null) {
            parallaxSystem.render(gc);
        }

        setupBrickReveal();

        drawBall(gc);
        drawPaddle(gc);
        drawBricks(gc);
        drawPowerUps(gc);
        drawWalls(gc);
        EffectRenderer.getInstance().render(gc);
    }

    public void update(double deltaTime) {
        parallaxSystem.update(deltaTime);
        EffectRenderer.getInstance().update(deltaTime);
        setupBrickReveal();
    }

    private void setupBrickReveal() {
        currentRevealTick++;

        if (reveal) {
            int brickRevealInterval = 5;
            if (currentRevealTick >= brickRevealInterval) {
                currentRevealTick = 0;
                Brick[] bricks = world.getBricks();
                if (bricks != null && brickRevealCounter < bricks.length) {
                    revealedBricks.add(bricks[brickRevealCounter]);
                    brickRevealCounter++;
                }
            }
        } else {
            Brick[] bricks = world.getBricks();
            if (bricks != null) {
                revealedBricks.clear();
                revealedBricks.addAll(Arrays.asList(bricks));
                brickRevealCounter = bricks.length;
            }
        }
    }

    private void drawBall(GraphicsContext gc) {
        Ball ball = world.getBall();
        if (ball != null)
            gc.drawImage(ball.getImage(), ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
    }

    private void drawPaddle(GraphicsContext gc) {
        Paddle paddle = world.getPaddle();
        if (paddle != null)
            gc.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
    }

    private void drawBricks(GraphicsContext gc) {
        Brick[] bricks = world.getBricks();
        if (bricks != null) {
            for (Brick brick : bricks) {
                if (revealedBricks.contains(brick) && !brick.isDestroyed()) {
                    gc.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                            brick.getWidth(), brick.getHeight());
                }
            }
        }
    }

    private void drawPowerUps(GraphicsContext gc) {
        for (PowerUp p : world.getPowerUps()) {
            if (p.isVisible()) {
                p.getAnimation().render(gc, p.getX(), p.getY(), p.getWidth(), p.getHeight());
            }
        }
    }

    private void drawWalls(GraphicsContext gc) {
        for (Wall wall : world.getWalls()) {
            gc.drawImage(wall.getImage(), wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
        }
    }

    public void reset() {
        revealedBricks.clear();
        brickRevealCounter = 0;
        currentRevealTick = 0;
    }

    public void initParallax() {
        parallaxSystem = new ParallaxSystem(
                world,
                GameVar.PARALLAX_BASE_SPEED,
                GameVar.PARALLAX_DEPTH,
                GameVar.PARALLAX_SPEED_LAYERS
        );

        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER1, GameVar.PARALLAX_SPEED_LAYERS[3]));
        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER2, GameVar.PARALLAX_SPEED_LAYERS[2]));
        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER3, GameVar.PARALLAX_SPEED_LAYERS[1]));
        parallaxSystem.addLayer(new ParallaxLayer(AssetPaths.LAYER4, GameVar.PARALLAX_SPEED_LAYERS[0]));
    }

    public GameWorld getWorld() {
        return world;
    }
}
