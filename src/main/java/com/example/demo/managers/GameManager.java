package com.example.demo.managers;

import com.example.demo.core.Ball;
import com.example.demo.core.Brick;
import com.example.demo.core.Paddle;
import com.example.demo.core.VARIABLES;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameManager extends Pane {

    private AnimationTimer timer;
    private String message = "Game Over";
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks;
    private boolean inGame = true;

    private GraphicsContext gc;

    public GameManager() {
        initBoard();
    }

    private void initBoard() {
        setPrefSize(VARIABLES.WIDTH, VARIABLES.HEIGHT);

        // Canvas for drawing
        Canvas canvas = new Canvas(VARIABLES.WIDTH, VARIABLES.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        // Enable key events
        setFocusTraversable(true);
        requestFocus();
        initKeyHandlers();

        gameInit();
    }

    private void initKeyHandlers() {
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                paddle.setDx(-4);
            } else if (e.getCode() == KeyCode.RIGHT) {
                paddle.setDx(4);
            }
        });

        setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                paddle.setDx(0);
            }
        });
    }

    private void gameInit() {
        bricks = new Brick[VARIABLES.N_OF_BRICKS];
        ball = new Ball();
        paddle = new Paddle();

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
            checkCollision();
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
                paddle.getWidth(), paddle.getHeight());

        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                gc.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                        brick.getWidth(), brick.getHeight());
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
        // Ball fell below bottom
//        if (ball.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) {
//            stopGame();
//        }

        // Win condition
        long destroyed = java.util.Arrays.stream(bricks)
                .filter(Brick::isDestroyed).count();
        if (destroyed == VARIABLES.N_OF_BRICKS) {
            message = "Victory";
            stopGame();
        }

        // Paddle collision
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

        // Brick collisions (simplified: just reverse Y and destroy brick)
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.getBounds().intersects(brick.getBounds())) {
                brick.setDestroyed(true);
                ball.setYDir(-ball.getYDir());
            }
        }
    }
}
