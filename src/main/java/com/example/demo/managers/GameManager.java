package com.example.demo.managers;

import com.almasb.fxgl.audio.Sound;
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

import com.example.demo.managers.SoundManager;

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
    private static final long paddleSoundCooldown = 200L;
    private long nextPaddleSoundTime = 0;
    private List<Wall> walls = new ArrayList();

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

        walls.clear();

        // Left walls
        for (int i = 0; i < VARIABLES.N_OF_WALLS_LEFT_RIGHT; i++) {
            walls.add(new Wall(Wall.Side.LEFT, 0, i * VARIABLES.HEIGHT_OF_WALLS, VARIABLES.WIDTH_OF_WALLS, VARIABLES.HEIGHT_OF_WALLS));
        }

        // Right walls
        for (int i = 0; i < VARIABLES.N_OF_WALLS_LEFT_RIGHT; i++) {
            walls.add(new Wall(Wall.Side.RIGHT, VARIABLES.WIDTH - VARIABLES.WIDTH_OF_WALLS, i * VARIABLES.HEIGHT_OF_WALLS, VARIABLES.WIDTH_OF_WALLS, VARIABLES.HEIGHT_OF_WALLS));
        }

        // Top walls
        for (int i = 0; i < VARIABLES.N_OF_WALLS_TOP; i++) {
            walls.add(new Wall(Wall.Side.TOP, i * VARIABLES.WIDTH_OF_WALLS, 0, VARIABLES.WIDTH_OF_WALLS, VARIABLES.HEIGHT_OF_WALLS));
        }


        //khởi tạo gạch
        for (int i = 0; i < VARIABLES.N_OF_BRICKS; i++) {
            int row = i / VARIABLES.BRICKS_PER_ROW;
            int col = i % VARIABLES.BRICKS_PER_ROW;
            int x = VARIABLES.FIRST_X_OF_BRICKS + col * (VARIABLES.WIDTH_OF_BRICKS + VARIABLES.PADDING_X);
            int y = VARIABLES.FIRST_Y_OF_BRICKS + row * (VARIABLES.HEIGHT_OF_BRICKS + VARIABLES.PADDING_Y);
            bricks[i] = new Brick(x, y);
        }

        SoundManager.getInstance().playRandomMusic(); //play blackground music


        loop();
    }

    private void loop() {
        final double FPS = 60.0;
        final double UPDATE_INTERVAL = 1e9 / FPS;
        final long[] lastUpate = {System.nanoTime()};

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (now - lastUpate[0] >= UPDATE_INTERVAL) {
                    update(1.0 / FPS);
                    lastUpate[0] += UPDATE_INTERVAL;
                }
                render();
            }
        };

        timer.start();
    }

    private void update(double deltaTime) {
        if (!inGame) {
            return;
        }

        ball.update(deltaTime);
        paddle.update(deltaTime);

        for (PowerUp p : activePowerUps) {
            if (p.isVisible()) {
                p.update(deltaTime);
            }
        }

        checkCollision();
        ball.updatePowerUps();
    }

    private void render() {
        gc.clearRect(0,0, VARIABLES.WIDTH, VARIABLES.HEIGHT);

        if (inGame) {
            drawObjects();
        }

        else {
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

        for (PowerUp p : activePowerUps) {
            if (p.isVisible()) {
                gc.drawImage(p.getImage(), p.getX(), p.getY(),
                        p.getWidth(), p.getHeight());
            }
        }
        //in tường
        for (Wall wall : walls){
            gc.drawImage(wall.getImage(), wall.getX(), wall.getY(),
                    wall.getWidth(), wall.getHeight());
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
        // stop background music
        SoundManager.getInstance().stopMusic();
    }

    private Collision buildCollision(GameObject a, GameObject b) {
        if (a == b) return null;
        if(!a.getBounds().intersects(b.getBounds())) return null;

        double overlapX = Math.min(a.getBounds().getMaxX(), b.getBounds().getMaxX())
                - Math.max(a.getBounds().getMinX(), b.getBounds().getMinX());
        double overlapY = Math.min(a.getBounds().getMaxY(), b.getBounds().getMaxY())
                - Math.max(a.getBounds().getMinY(), b.getBounds().getMinY());

        return new Collision(a, b, System.nanoTime(), overlapX, overlapY);
    }

    // add hiệu ứng vào checkCollision
    private void checkCollision() {
        // Kiểm tra bóng rơi quá đáy màn hình
        if (ball.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) {
            SoundManager.getInstance().playSound("game_over");
            ball.resetState();
        }

        // Win condition
        long destroyed = java.util.Arrays.stream(bricks)
                .filter(Brick::isDestroyed).count();

        // Va chạm giữa power-up và thanh đỡ (paddle)
        for (PowerUp p : activePowerUps) {
            Collision c = buildCollision(p, paddle);
            if (c != null && p.isVisible()) {
                SoundManager.getInstance().playSound("power_up");
                ball.activatePowerUp(p);
                p.setVisible(false);
            }

            if (p.isVisible() && p.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) {
                p.setVisible(false);
            }
        }

        // Va chạm giữa bóng và thanh đỡ
        Collision paddleCollision = buildCollision(ball, paddle);
        if (paddleCollision != null) {
            if (!ball.isStuck()) {
                long now = System.currentTimeMillis();
                if (now > nextPaddleSoundTime) {
                    SoundManager.getInstance().playSound("paddle_hit");
                    nextPaddleSoundTime = now + paddleSoundCooldown;
                }
            }

            ball.setPosition(ball.getX(), paddle.getBounds().getMinY() - ball.getHeight());

            double paddleLPos = paddle.getBounds().getMinX();
            double ballCenterX = ball.getBounds().getMinX() + ball.getWidth() / 2.0;
            double hitPos = (ballCenterX - paddleLPos) / paddle.getWidth();

            double angle = Math.toRadians(150 * (1 - hitPos) + 30 * hitPos);
            ball.setVelocity(angle);
        }

        for(Wall wall : walls) {
            Collision c = buildCollision(ball, wall);
            if (c != null) {
                SoundManager.getInstance().playSound("wall_hit");
                switch (wall.getSide()) {
                    case LEFT:
                        ball.setDx(Math.abs(ball.getDx()));
                        break;
                    case RIGHT:
                        ball.setDx(-Math.abs(ball.getDx()));
                        break;
                    case TOP:
                        ball.setDy(Math.abs(ball.getDy()));
                        break;
                }
            }
        }

        // Va chạm với gạch
        for (Brick brick : bricks) {
            Collision c = buildCollision(ball, brick);
            if (c != null && !brick.isDestroyed()) {
                brick.setDestroyed(true);
                SoundManager.getInstance().playSound("brick_hit");

                boolean ballFromSide = c.getOverlapX() < c.getOverlapY();
                if (ballFromSide) {
                    ball.setDx(-ball.getDx());
                } else {
                    ball.setDy(-ball.getDy());
                }

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