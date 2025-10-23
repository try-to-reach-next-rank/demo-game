package com.example.demo.view;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Renderable;
import com.example.demo.model.core.Ball;
import com.example.demo.model.core.Paddle;
import com.example.demo.model.core.PowerUp;
import com.example.demo.model.core.Wall;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.utils.GlobalVar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.HashSet;
import java.util.Set;

public class Renderer implements Renderable {
    private final GameWorld world;
    private Set<Brick> revealedBricks = new HashSet<>();
    private int brickRevealCounter = 0;
    private final int brickRevealInterval = 5;
    private int currentRevealTick = 0;

    public Renderer(GameWorld world) {
        this.world = world;
    }

    @Override
    public void render(GraphicsContext gc) {

        currentRevealTick++;
        if (currentRevealTick >= brickRevealInterval) {
            currentRevealTick = 0;
            Brick[] bricks = world.getBricks();
            if (bricks != null && brickRevealCounter < bricks.length) {
                revealedBricks.add(bricks[brickRevealCounter]);
                brickRevealCounter++;
            }
        }

        // 1. Draw Ball
        Ball ball = world.getBall();
        if (ball != null) {
            Image ballImage = ball.getImage();
            gc.drawImage(ballImage, ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
        }

        // 2. Draw Paddle
        Paddle paddle = world.getPaddle();
        if (paddle != null) {
            Image paddleImage = paddle.getImage();
            gc.drawImage(paddleImage, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
        }

        // 3. Draw Bricks
        Brick[] bricks = world.getBricks();
        if (bricks != null) {
            for (Brick brick : bricks) {
                if (revealedBricks.contains(brick) && !brick.isDestroyed()) {
                    gc.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                            brick.getWidth(), brick.getHeight());
                }
            }
        }

        // 4. Draw PowerUps
        for (PowerUp p : world.getPowerUps()) {
            if (p.isVisible()) {
                p.getAnimation().render(gc, p.getX(), p.getY(), p.getWidth(), p.getHeight());
            }
        }

        // 5. Draw Walls
        for (Wall wall : world.getWalls()) {
            gc.drawImage(wall.getImage(), wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
        }

        // 6. Draw Effects (delegated to the singleton EffectRenderer)
        EffectRenderer.getInstance().render(gc);
    }

    public void reset() {
        revealedBricks.clear();
        brickRevealCounter = 0;
        currentRevealTick = 0;
    }
}
