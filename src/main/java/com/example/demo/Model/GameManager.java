package com.example.demo.Model;

import com.example.demo.Controller.core.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Import các lớp quản lý map mới


public class GameManager extends Pane {

    private                     AnimationTimer timer;
    private Ball ball;
    private Paddle paddle;
    private                     Brick[] bricks;
    private boolean             inGame = true;
    private final               Random random = new Random();
    private                     GraphicsContext gc;
    private final List<PowerUp> activePowerUps = new ArrayList<>();
    private final List<Wall>    walls = new ArrayList<>();
    private final UIManager     uiManager = new UIManager();
    private final DialogueBox dialogueBox = new DialogueBox();
    private final CameraManager cameraManager = new CameraManager(0.15, 8.0, new double[]{1.0, 0.6, 0.35, 0.2});
    private final CollisionManager collisionManager = new CollisionManager();
    private final RenderManager renderManager = new RenderManager(VARIABLES.WIDTH, VARIABLES.HEIGHT);

    // THUỘC TÍNH MỚI CHO VIỆC QUẢN LÝ MAP VÀ LEVEL
    private final MapManager    mapManager = new MapManager();
    private int                 currentLevel = 1;
    private final List<ParallaxLayer> parallaxLayers = new ArrayList<>();

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

        // Khởi tạo các đối tượng và tải level
        gameInit();
    }

    private void gameInit() {
        paddle = new Paddle();
        ball = new Ball(paddle);
        uiManager.add(dialogueBox);
        dialogueBox.start(new String[] {
                "This is a test from the developers... :3",
                "Someone is literally spending time reading this",
                "This dialogue can only show short text ¯\\_(ツ)_/¯"
        });

        // Tải Level 1 bằng MapManager (thay thế logic khởi tạo gạch/tường cũ)
        loadLevel(currentLevel);

        SoundManager.getInstance().playRandomMusic(); //play background music
        if(currentLevel == 1) {
            ParallaxLayer l1 = new ParallaxLayer("/images/layer1.png", 0.25);
            ParallaxLayer l2 = new ParallaxLayer("/images/layer2.png", 0.50);
            ParallaxLayer l3 = new ParallaxLayer("/images/layer3.png", 0.75);
            ParallaxLayer l4 = new ParallaxLayer("/images/layer4.png", 1.00);

            // make them wider than screen to avoid gaps
            l1.setWrapWidth(VARIABLES.WIDTH * 1.5);
            l2.setWrapWidth(VARIABLES.WIDTH * 1.6);
            l3.setWrapWidth(VARIABLES.WIDTH * 1.7);
            l4.setWrapWidth(VARIABLES.WIDTH * 1.8);

            parallaxLayers.add(l1);
            parallaxLayers.add(l2);
            parallaxLayers.add(l3);
            parallaxLayers.add(l4);
        }
        loop();
    }

    /**
     * Tải map mới dựa trên số level.
     * Level 1-3: Map cố định. Level 4 trở đi: Map ngẫu nhiên.
     */
    private void loadLevel(int level) {
        MapData mapData;

        if (level <= 3) {
            mapData = mapManager.loadMap(level);
        } else {
            // Sử dụng map ngẫu nhiên cho các level sau level cố định
            mapData = mapManager.loadMapFromMatrix(mapManager.createRandomMatrix());
        }

        // 1. Cập nhật Tường
        walls.clear();
        walls.addAll(mapData.walls());

        // 2. Cập nhật Gạch
        List<Brick> brickList = mapData.bricks();
        bricks = brickList.toArray(new Brick[0]); // Chuyển List thành Array

        // 3. Xóa Power-up cũ
        activePowerUps.clear();

        // 4. Reset Ball và Paddle
        ball.resetState();
        paddle.resetState();

        // 5. Reset effects
        EffectManager.getInstance().clear();
    }

    private void loop() {
        final double FPS = VARIABLES.FPS;
        final double UPDATE_INTERVAL = 1e9 / FPS;
        final long[] lastUpate = {System.nanoTime()};

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (now - lastUpate[0] >= UPDATE_INTERVAL) {
                    update(1.0 / FPS);
                    lastUpate[0] += (long) UPDATE_INTERVAL;
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

        uiManager.update(deltaTime);

        if(!uiManager.hasActiveUI() && inGame) {
            ball.update(deltaTime);
            paddle.update(deltaTime);
        if (!parallaxLayers.isEmpty()) {
            // 1. Tính toán vị trí chuẩn hóa của Paddle (normalized Paddle X)
            double paddleMinX = VARIABLES.WIDTH_OF_WALLS;
            double paddleMaxX = VARIABLES.WIDTH - VARIABLES.WIDTH_OF_WALLS - paddle.getWidth();
            double paddleRange = paddleMaxX - paddleMinX;

            double normalizedPaddleX = (paddle.getX() - paddleMinX) / paddleRange;
            normalizedPaddleX = Math.max(0.0, Math.min(1.0, normalizedPaddleX));

            cameraManager.update(normalizedPaddleX, VARIABLES.WIDTH, parallaxLayers, deltaTime);

            // update animation frames for each layer (separate responsibility)
            for (ParallaxLayer layer : parallaxLayers) {
                layer.update(deltaTime);
            }
        }
            // Cập nhật vị trí PowerUp
            for (PowerUp p : activePowerUps) {
                if (p.isVisible()) {
                    p.update(deltaTime);
                }
            }

            collisionManager.update(ball, paddle, bricks, activePowerUps, walls);
            ball.updatePowerUps();

        // Update effects
        EffectManager.getInstance().update(deltaTime);
        }
    }

    private void render() {
        gc.clearRect(0,0, VARIABLES.WIDTH, VARIABLES.HEIGHT);

        if (inGame) {
            drawObjects();
            drawEffects();
        }
        else {
            gameFinished();
        }

        uiManager.render(gc, VARIABLES.WIDTH, VARIABLES.HEIGHT);
    }

    private void drawObjects() {
        renderManager.drawParallax(gc, parallaxLayers);

        gc.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getWidth(), ball.getHeight());

        // Vẽ thanh đỡ
        gc.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                paddle.getWidth(), paddle.getHeight());

        // Vẽ gạch
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                gc.drawImage(brick.getImage(), brick.getX(), brick.getY(),
                        brick.getWidth(), brick.getHeight());
            }
        }

        // Vẽ PowerUp
        for (PowerUp p : activePowerUps) {
            if (p.isVisible()) {
                gc.drawImage(p.getImage(), p.getX(), p.getY(),
                        p.getWidth(), p.getHeight());
            }
        }

        // Vẽ tường
        for (Wall wall : walls){
            gc.drawImage(wall.getImage(), wall.getX(), wall.getY(),
                    wall.getWidth(), wall.getHeight());
        }
    }

    private void drawEffects() {
        EffectManager.getInstance().draw(gc);
    }

    private void gameFinished() {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Verdana", 18));
        String message = "Game Over";
        gc.fillText(message,
                (VARIABLES.WIDTH - message.length() * 10) / 2.0,
                VARIABLES.HEIGHT / 2.0);
    }

    private void stopGame() {
        inGame = false;
        timer.stop();
        // stop background music
        SoundManager.getInstance().stopMusic();

        // Clear all effects
        EffectManager.getInstance().clear();
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public Ball getBall() {
        return ball;
    }

    public UIManager getUIManager() {
        return uiManager;
    }
}
