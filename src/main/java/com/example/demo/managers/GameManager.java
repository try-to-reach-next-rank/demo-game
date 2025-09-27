package com.example.demo.managers;

import com.example.demo.core.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager extends Pane {

    private                     AnimationTimer timer;
    private                     String message = "Game Over";
    private                     Ball ball;
    private                     Paddle paddle;
    private                     Brick[] bricks;
    private boolean             inGame = true;
    private final               Random random = new Random();
    private                     GraphicsContext gc;
    private final List<PowerUp> activePowerUps = new ArrayList<>();

    public GameManager() {
        initBoard();
    }

    private void initBoard() {
        setPrefSize(VARIABLES.WIDTH, VARIABLES.HEIGHT);
        Canvas canvas = new Canvas(VARIABLES.WIDTH, VARIABLES.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setFocusTraversable(true);
        requestFocus();
        gameInit();
    }

    private void gameInit() {
        bricks = new Brick[VARIABLES.N_OF_BRICKS];
        paddle = new Paddle();
        ball = new Ball(paddle);

        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                bricks[k] = new Brick(j * 40 + 30, i * 20 + 50);
                k++;
            }
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                doGameCycle();
            }
        };
        timer.start();
    }

    private void doGameCycle() {
        if (inGame) {
            ball.move();
            paddle.move();

            for (PowerUp p : activePowerUps) {
                if (p.isVisible()) {
                    p.move();
                }
            }

            checkCollision();
            ball.updatePowerUps();
        }
        draw();
    }

    private void draw() {
        gc.clearRect(0, 0, VARIABLES.WIDTH, VARIABLES.HEIGHT);

        if (inGame) {
            drawObjects();
        } else {
            gameFinished();
        }
    }

    private void drawObjects() {
        gc.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getWidth(), ball.getHeight());

        gc.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getWidth() * 2, paddle.getHeight() * 2);

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                gc.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                        brick.getWidth(), brick.getHeight());
            }
        }

        for (PowerUp p : activePowerUps) {
            if (p.isVisible()) {
                gc.drawImage(p.getImage(), p.getX(), p.getY(),
                        p.getWidth(), p.getHeight());
            }
        }
    }

    private void gameFinished() {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Verdana", 18));
        gc.fillText(message,
                (VARIABLES.WIDTH - message.length() * 10) / 2.0,
                VARIABLES.HEIGHT / 2.0);
    }

    private void stopGame() {
        inGame = false;
        timer.stop();
    }

    private void checkCollision() {
        // Kiểm tra bóng rơi quá đáy màn hình
        if (ball.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) {
//            stopGame();
            ball.resetState();
        }

        // Win condition
        long destroyed = java.util.Arrays.stream(bricks)
                .filter(Brick::isDestroyed).count();

        // Va chạm giữa power-up và thanh đỡ (paddle)
        for (PowerUp p : activePowerUps) {
            if (p.isVisible() && p.getBounds().intersects(paddle.getBounds())) {
                ball.activatePowerUp(p); // let Ball handle effect
                p.setVisible(false);
            }

            // Power-up falls off screen
            if (p.isVisible() && p.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) {
                p.setVisible(false);
            }
        }

        // Va chạm giữa bóng và thanh đỡ
        if (ball.getBounds().intersects(paddle.getBounds())) {
            double paddleLPos = paddle.getBounds().getMinX();
            double ballLPos = ball.getBounds().getMinX();

            double first = paddleLPos + paddle.getWidth() * 0.2;
            double second = paddleLPos + paddle.getWidth() * 0.4;
            double third = paddleLPos + paddle.getWidth() * 0.6;
            double fourth = paddleLPos + paddle.getWidth() * 0.8;

            if (ballLPos < first) {
                ball.setXDir(-1);
                ball.setYDir(-1);
            } else if (ballLPos < second) {
                ball.setXDir(-1);
                ball.setYDir(-ball.getYDir());
            } else if (ballLPos < third) {
                ball.setXDir(0);
                ball.setYDir(-1);
            } else if (ballLPos < fourth) {
                ball.setXDir(1);
                ball.setYDir(-ball.getYDir());
            } else {
                ball.setXDir(1);
                ball.setYDir(-1);
            }
        }

        // Va chạm với gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.getBounds().intersects(brick.getBounds())) {
                brick.setDestroyed(true);
                ball.setYDir(-ball.getYDir());

                if (random.nextInt(100) < 30) {
                    PowerUp newPU = new PowerUp("ACCELERATE");
                    newPU.dropFrom(brick);
                    activePowerUps.add(newPU);
                }
            }
        }

        if (destroyed == VARIABLES.N_OF_BRICKS) {
            message = "Victory";
            stopGame();
        }
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public Ball getBall() {
        return ball;
    }
}