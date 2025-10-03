package com.example.demo.managers;

import com.almasb.fxgl.audio.Sound; // Giữ lại nếu bạn đang sử dụng FXGL
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
import java.util.Iterator;

// Import các lớp quản lý map mới
import com.example.demo.managers.SoundManager;
import com.example.demo.managers.MapManager;
import com.example.demo.core.MapData;
import com.example.demo.core.ParallaxLayer;

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
    private static final long   paddleSoundCooldown = 200L;
    private long                nextPaddleSoundTime = 0;
    private List<Wall>          walls = new ArrayList();

    // THUỘC TÍNH MỚI CHO VIỆC QUẢN LÝ MAP VÀ LEVEL
    private final MapManager    mapManager = new MapManager();
    private int                 currentLevel = 1;

    private List<ParallaxLayer> parallaxLayers = new ArrayList<>();
    private final double MAX_PARALLAX_OFFSET = 300.0;

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

        // Tải Level 1 bằng MapManager (thay thế logic khởi tạo gạch/tường cũ)
        loadLevel(currentLevel);

        SoundManager.getInstance().playRandomMusic(); //play background music
        if(currentLevel == 1) {
            parallaxLayers.add(new ParallaxLayer("/images/layer1.png", 0.25));

            parallaxLayers.add(new ParallaxLayer("/images/layer2.png", 0.50));


            parallaxLayers.add(new ParallaxLayer("/images/layer3.png", 0.75));

            parallaxLayers.add(new ParallaxLayer("/images/layer4.png", 1.00));
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
        if (!parallaxLayers.isEmpty()) {
            // 1. Tính toán vị trí chuẩn hóa của Paddle (normalized Paddle X)
            double paddleMinX = VARIABLES.WIDTH_OF_WALLS;
            double paddleMaxX = VARIABLES.WIDTH - VARIABLES.WIDTH_OF_WALLS - paddle.getWidth();
            double paddleRange = paddleMaxX - paddleMinX;

            double normalizedPaddleX = (paddle.getX() - paddleMinX) / paddleRange;
            // Giới hạn giá trị trong khoảng [0, 1]
            normalizedPaddleX = Math.max(0.0, Math.min(1.0, normalizedPaddleX));

            // 2. Cập nhật và Áp dụng Parallax/Animation cho từng Layer
            for (ParallaxLayer layer : parallaxLayers) {

                // A. Kích hoạt Animation (Chuyển khung hình nếu layer là động)
                layer.update(deltaTime);

                // B. Tính toán và Áp dụng Parallax (Thay đổi vị trí X)
                double newX = layer.getParallaxX(MAX_PARALLAX_OFFSET, normalizedPaddleX);
                layer.setXOffset(newX);
            }
        }
        // Cập nhật vị trí PowerUp
        Iterator<PowerUp> puIterator = activePowerUps.iterator();
        while (puIterator.hasNext()) {
            PowerUp p = puIterator.next();
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
        // Vẽ bóng

        for (ParallaxLayer layer : parallaxLayers) {
            gc.drawImage(layer.getImage(), layer.getX(), layer.getY(),
                    layer.getWidth(), layer.getHeight());
        }

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

    private void checkCollision() {
        // Kiểm tra bóng rơi quá đáy màn hình
        if (ball.getBounds().getMaxY() > VARIABLES.BOTTOM_EDGE) {
            SoundManager.getInstance().playSound("game_over");
            // Logic tạm thời: Reset bi khi rớt. Bạn nên thêm logic mạng (lives) tại đây.
            ball.resetState();
        }

        // Đếm gạch đã phá
        long destroyed = java.util.Arrays.stream(bricks)
                .filter(Brick::isDestroyed).count();

        // Va chạm giữa power-up và thanh đỡ (paddle)
        Iterator<PowerUp> puIterator = activePowerUps.iterator();
        while(puIterator.hasNext()) {
            PowerUp p = puIterator.next();
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
            ball.setVelocity(new Vector2D(Math.cos(angle), -Math.sin(angle)));
        }

        // Va chạm với tường
        for(Wall wall : walls) {
            Collision c = buildCollision(ball, wall);
            if (c != null) {
                SoundManager.getInstance().playSound("wall_hit");
                Vector2D v = ball.getVelocity();
                switch (wall.getSide()) {
                    case LEFT:
                        ball.setVelocity(new Vector2D(Math.abs(v.x), v.y));
                        break;
                    case RIGHT:
                        ball.setVelocity(new Vector2D(-Math.abs(v.x), v.y));
                        break;
                    case TOP:
                        ball.setVelocity(new Vector2D(v.x, Math.abs(v.y)));
                        break;
                }
            }
        }

        // Va chạm với gạch
        for (Brick brick : bricks) {
            Collision c = buildCollision(ball, brick);
            if (c != null && !brick.isDestroyed()) {
               // brick.setDestroyed(true); xóa gạch cũ thay bằng giảm máu 1 lần
                //gọi sound từ function phá gạch
                String soundToPlay = brick.takeDamage();

                if (soundToPlay != null) {
                    if("explosion_hit".equals(soundToPlay)) handleExplosion(brick);
                    SoundManager.getInstance().playSound(soundToPlay);
                }

                boolean ballFromSide = c.getOverlapX() < c.getOverlapY();
                Vector2D v = ball.getVelocity();

                if (ballFromSide) {
                    ball.setVelocity(new Vector2D(-v.x, v.y));
                } else {
                    ball.setVelocity(new Vector2D(v.x, -v.y));
                }

                if (brick.isDestroyed() && random.nextInt(100) < 30) {
                    PowerUp newPU = new PowerUp("ACCELERATE");
                    newPU.dropFrom(brick);
                    activePowerUps.add(newPU);
                }
            }
        }

        // Logic Chuyển Level (Win condition)
        if (bricks.length > 0 && destroyed == bricks.length) {
            currentLevel++;

            // Tải level tiếp theo
            loadLevel(currentLevel);

            if (currentLevel <= 3) {
                message = "Level " + currentLevel;
            } else {
                message = "Random Level " + (currentLevel - 3);
            }

            SoundManager.getInstance().playSound("level_up");

            // Không gọi stopGame() để tiếp tục chơi
        }
    }

    private void handleExplosion(Brick sourceBrick) {
        // tọa độ tâm của vụ nổ
        double centerX = sourceBrick.getX() + sourceBrick.getWidth() / 2;
        double centerY = sourceBrick.getY() + sourceBrick.getHeight() / 2;

        // Bán kính vụ nổ ( điều chỉnh giá trị 2.5)
        double radius = sourceBrick.getWidth() * 2.5;

        // Duyệt qua tất cả các viên gạch
        for (Brick otherBrick : bricks) {
            // Bỏ qua self và những viên đã bị phá hủy
            if (otherBrick == sourceBrick || otherBrick.isDestroyed()) {
                continue;
            }

            // Tâm gạch tiếp theo
            double otherCenterX = otherBrick.getX() + otherBrick.getWidth() / 2;
            double otherCenterY = otherBrick.getY() + otherBrick.getHeight() / 2;

            // Tính khoảng cách 2 gạch
            double distance = Math.sqrt(Math.pow(centerX - otherCenterX, 2) + Math.pow(centerY - otherCenterY, 2));

            // Nếu nằm trong bán kính vụ nổ = gọi takeDamage
            if (distance <= radius) {
                    otherBrick.takeDamage();
                }
            }
        }

    public Paddle getPaddle() {
        return paddle;
    }

    public Ball getBall() {
        return ball;
    }
}
