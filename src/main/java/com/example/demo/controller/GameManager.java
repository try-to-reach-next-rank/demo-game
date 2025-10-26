package com.example.demo.controller;

import com.example.demo.engine.*;
import com.example.demo.model.core.*;
import com.example.demo.model.core.effects.TransitionEffect;
import com.example.demo.model.map.MapData;
import com.example.demo.model.state.*;
import com.example.demo.model.system.*;
import com.example.demo.model.utils.CheatTable;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Sound;
import com.example.demo.model.utils.dialogue.DialogueBox;
import com.example.demo.model.utils.dialogue.DialogueSystem;
import com.example.demo.view.*;
import com.example.demo.model.map.ParallaxLayer;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.model.utils.GlobalVar.SAVE_FILE_NAME;
import static com.example.demo.model.utils.GlobalVar.SECRET_CODE;

public class GameManager extends Pane {

    private AnimationTimer timer;
    private final GraphicsContext gc;

    // === MODEL ===
    private final GameWorld world = new GameWorld();

    private static final Logger log = LoggerFactory.getLogger(GameManager.class);

    // === SYSTEMS ===
    private final List<Updatable> updatables = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();

    private final UIManager uiManager = new UIManager();
    private final DialogueBox dialogueBox = new DialogueBox();

    private final MapManager mapManager = new MapManager();
    private CollisionManager collisionManager;
    private BrickSystem brickSystem;
    private DialogueSystem dialogueSystem; /** new dialogue system for running dialogue through txt*/
    private ParallaxSystem parallaxSystem;
    private PowerUpSystem powerUpSystem;
    private Renderer renderer;
    private final TransitionEffect transitionEffect = new TransitionEffect(2.0); // 1.5s duration
    private CheatTable cheatTable;


    private boolean inGame = true;
    private final StringBuilder keySequence = new StringBuilder();

    public GameManager() {
        setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        Canvas canvas = new Canvas(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setFocusTraversable(true);
        requestFocus();

        // Lấy các lệnh từ intro.txt rồi gọi dialogue tương ứng thông qua dialogueBox
        dialogueSystem = new DialogueSystem("/Dialogue/intro.txt", dialogueBox);
        setupSecretCodeEasterEgg();

        uiManager.add(dialogueBox);
        initGame();
    }

    // -------------------------------------------------------------------------
    //  Game Initialization
    // -------------------------------------------------------------------------

    private void initGame() {
        // --- Create base entities (Model layer) ---
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);
        world.setPaddle(paddle);
        world.setBall(ball);

        // --- Create core systems (Controller layer) ---
        BallSystem ballSystem = new BallSystem(ball, paddle);
        PaddleSystem paddleSystem = new PaddleSystem(paddle);
        // FROM: PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, world.getPowerUps());
        // to:
        this.powerUpSystem = new PowerUpSystem(ball, paddle, world.getPowerUps());

        // Give the GameWorld the reference to the PowerUpSystem
        world.setPowerUpSystem(this.powerUpSystem);

        // --- Load map and build bricks/walls ---
        loadLevel(world.getCurrentLevel());
        brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());

        // --- Create managers (controllers) --- // TODO: maybe simplify or get rid of this
        collisionManager = new CollisionManager(world, ballSystem, brickSystem, powerUpSystem);

        // --- Register update systems ---
        updatables.addAll(List.of(
                paddleSystem,
                ballSystem,
                powerUpSystem,
                brickSystem,
                collisionManager
        ));

        // --- Register renderables (View layer) ---
        this.renderer = new Renderer(world);
        renderables.add(renderer);                                               // then the world
        renderables.add((gc) -> uiManager.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT)); // UI last

        // --- Setup parallax for first level ---
        if (world.getCurrentLevel() == 2) initParallax();

        // --- INTRO ---
        dialogueSystem.start();
        Sound.getInstance().playRandomMusic();
        loop();
    }

    // -------------------------------------------------------------------------
    //  Level Management
    // -------------------------------------------------------------------------

    private void loadLevel(int level) { // TODO: put this in map
        inGame = false;
        transitionEffect.start(
                // midpoint -> when screen is fully black
                () -> {
                    MapData mapData = switch (level) {
                        case 1 -> mapManager.loadMap(1);
                        case 2 -> mapManager.loadMap(2);
                        case 3 -> mapManager.loadMap(3);
                        default -> mapManager.loadMap(1);
                    };

                    world.getWalls().clear();
                    world.getWalls().addAll(mapData.getWalls());

                    List<Brick> bricks = mapData.getBricks();
                    world.setBricks(bricks.toArray(new Brick[0]));
                    world.resetForNewLevel();

                    renderer.reset();
                    this.brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());

                    if (parallaxSystem == null && world.getCurrentLevel() == 1) initParallax();
                },

                // end -> fade finished, resume gameplay + dialogue
                () -> {
                    inGame = true;
                    MapData mapData = mapManager.loadMap(level);
                    log.info("Loaded map: {} with {} bricks", level, mapData.getBricks().size());
                    dialogueSystem.start();
                }
        );
    }

    public void loadNextLevel() {
        int currentLevel = world.getCurrentLevel();
        int nextLevel = currentLevel + 1;

        if (nextLevel > 3) {
            nextLevel = 1; // Quay vòng
        }

        world.setCurrentLevel(nextLevel); // Giả sử GameWorld có hàm này
        loadLevel(nextLevel);
    }

    public void loadPreviousLevel() {
        int currentLevel = world.getCurrentLevel();
        int prevLevel = currentLevel - 1;

        if (prevLevel < 1) {
            prevLevel = 3; // Quay vòng
        }

        world.setCurrentLevel(prevLevel);
        loadLevel(prevLevel);
    }

    private void saveGame() {
        log.info("Bắt đầu lưu game...");
        // construct gameState để thu thập toàn bộ trạng thái game
        GameState gameState = new GameState(world);
        // 1 dòng để lưu
        SaveManager.save(gameState, SAVE_FILE_NAME);
    }

    private void loadGame() {
        log.info("Bắt đầu tải game...");
        // 1 dòng để tải
        GameState loadedState = SaveManager.load(SAVE_FILE_NAME, GameState.class);
        // Kiểm tra và gọi hàm áp dụng trạng thái
        if (loadedState != null) {
            applyState(loadedState);
            log.info("Tải game thành công!");
        } else {
            log.info("Không có file lưu hoặc file lỗi.");
        }
    }

    private void applyState(GameState loadedState) {
        // KHU VỰC 1: THIẾT LẬP NỀN TẢNG
        loadLevel(loadedState.getCurrentLevel());

        // KHU VỰC 2: RA LỆNH CHO CÁC ĐỐI TƯỢNG TỰ CẬP NHẬT
        Ball ball = world.getBall();
        Paddle paddle = world.getPaddle();
        Brick[] bricks = world.getBricks();

        paddle.applyState(loadedState.getPaddleData());
        ball.applyState(loadedState.getBallData());

        for (BrickData brickData : loadedState.getBricksData()) {
            int id = brickData.getId();
            if (id >= 0 && id < bricks.length) {
                bricks[id].applyState(brickData);
            }
        }

        // KHU VỰC 3: XỬ LÝ CÁC MỐI QUAN HỆ VÀ DANH SÁCH
        if (ball.isStuck()) {
            ball.alignWithPaddle(10, 1.0);
        }

        // falling Power-Ups
        world.getPowerUps().clear();
        for (PowerUpData powerUpData : loadedState.getPowerUpsData()) {
            PowerUp p = new PowerUp(powerUpData.getType());
            p.setPosition(powerUpData.getX(), powerUpData.getY());
            p.setVisible(powerUpData.isVisible());
            world.getPowerUps().add(p);
        }

        // Active Power-ups
        PowerUpSystem currentPowerUpSystem = world.getPowerUpSystem(); // Get it from the world
        if (currentPowerUpSystem != null) {
            currentPowerUpSystem.reset(); // Reset the system

            if (loadedState.getActivePowerUpsData() != null) {
                for (ActivePowerUpData activeData : loadedState.getActivePowerUpsData()) {
                    currentPowerUpSystem.activateFromSave(activeData);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    //  Parallax Setup
    // -------------------------------------------------------------------------

    private void initParallax() { // TODO: put this in parallax, not game manager
        parallaxSystem = new ParallaxSystem(world, 0.15, 8.0, new double[] {1.0, 0.6, 0.35, 0.2});

        parallaxSystem.addLayer(new ParallaxLayer("/images/layer1.png", 0.25));
        parallaxSystem.addLayer(new ParallaxLayer("/images/layer2.png", 0.5));
        parallaxSystem.addLayer(new ParallaxLayer("/images/layer3.png", 0.75));
        parallaxSystem.addLayer(new ParallaxLayer("/images/layer4.png", 1.0));
    }

    // -------------------------------------------------------------------------
    //  Game Loop
    // -------------------------------------------------------------------------

    private void loop() {
        timer = new AnimationTimer() {
            private long lastTime = System.nanoTime();
            private double fpsTimer = 0;
            private int frames = 0;

            @Override
            public void handle(long now) {
                double deltaTime = (now - lastTime) / 1e9;
                lastTime = now;

                if (deltaTime > 0.05) deltaTime = 0.05;

                update(deltaTime);
                render();

                fpsTimer += deltaTime;
                frames++;
                if (fpsTimer >= 1.0) {
                    log.info("FPS: {}", frames);
                    fpsTimer = 0;
                    frames = 0;
                }
            }
        };
        timer.start();
    }

    // -------------------------------------------------------------------------
    //  Update & Render
    // -------------------------------------------------------------------------

    private void update(double deltaTime) {
        // Always update transition effect and UI
        transitionEffect.update(deltaTime);
        uiManager.update(deltaTime);

        // Only skip gameplay logic, not transitions
        if (!inGame || uiManager.hasActiveUI()) return;

        // Continue updating gameplay systems
        if (parallaxSystem != null)
            parallaxSystem.update(deltaTime);

        for (Updatable system : updatables)
            system.update(deltaTime);

        EffectRenderer.getInstance().update(deltaTime);
    }


    private void render() {
        gc.clearRect(0, 0, GlobalVar.WIDTH, GlobalVar.HEIGHT);

        if (parallaxSystem != null)
            parallaxSystem.render(gc);

        for (Renderable r : renderables)
            r.render(gc);

        // UI always rendered, but optionally hide when transition is active
        if (!transitionEffect.isActive() && uiManager.hasActiveUI()) {
            uiManager.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT);
        }

        // Transition overlay on top
        transitionEffect.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT);
    }

    // -------------------------------------------------------------------------
    //  Misc
    // -------------------------------------------------------------------------

    private void setupSecretCodeEasterEgg() {
        setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == null) return;

            // Phím `~` (BACK_QUOTE) bây giờ CHỈ dùng để MỞ menu
            if (code == KeyCode.BACK_QUOTE) {
                if (cheatTable != null) {
                    cheatTable.show(); // Chỉ mở
                } else {
                    log.info("(Cheat menu not unlocked yet)");
                }
                e.consume();
                return;
            }

            if (code == KeyCode.F5) {
                saveGame();
                return;
            }
            if (code == KeyCode.F9) {
                loadGame();
                return;
            }

            // Logic cho secret code
            String key = code.getName().toUpperCase();
            keySequence.append(key);
            if (keySequence.length() > SECRET_CODE.length()) {
                keySequence.delete(0, keySequence.length() - SECRET_CODE.length());
            }

            if (keySequence.toString().equals(SECRET_CODE)) {

                if (this.cheatTable == null) {
                    log.info("!!! CHEAT MENU UNLOCKED !!!");
                    this.cheatTable = new CheatTable(this);
                        this.uiManager.add(this.cheatTable); // THÊM VÀO UIMANAGER
                }

                dialogueBox.start(new DialogueBox.DialogueLine[]{
                        new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.EGG, "You found the secret!"),
                        new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.BALL, "Cheat menu unlocked. Press ~ to open.")
                });

                keySequence.setLength(0);
                return;
            }

        });
    }

    private void gameFinished() {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Verdana", 18));
        if (!uiManager.contains(dialogueBox)) uiManager.add(dialogueBox);
        dialogueBox.start(new DialogueBox.DialogueLine[]{
                new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.EGG, "Round two? Let's go!"),
                new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.BALL, "Bring it on!")
        });
    }

    private void stopGame() {
        inGame = false;
        if (timer != null) timer.stop();
        Sound.getInstance().stopMusic();
        EffectRenderer.getInstance().clear();
    }

    // -------------------------------------------------------------------------
    //  Accessors
    // -------------------------------------------------------------------------

    public UIManager getUIManager() { return uiManager; }

    public Paddle getPaddle() {
        return world.getPaddle();
    }
    public Ball getBall() {
        return world.getBall();
    }
}