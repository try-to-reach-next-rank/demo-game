package com.example.demo.controller.core;

import com.example.demo.controller.map.MapController;
import com.example.demo.engine.*;
import com.example.demo.model.core.*;
import com.example.demo.model.core.effects.TransitionEffect;
import com.example.demo.model.state.*;
import com.example.demo.model.system.*;

import com.example.demo.utils.CheatTable;
import com.example.demo.utils.PauseTable;

import com.example.demo.utils.Sound;
import com.example.demo.utils.dialogue.DialogueBox;
import com.example.demo.utils.dialogue.DialogueSystem;
import com.example.demo.repository.SaveDataRepository;
import com.example.demo.utils.var.AssetPaths;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.*;
import com.example.demo.model.map.ParallaxLayer;
import javafx.animation.AnimationTimer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.demo.utils.var.GlobalVar.SECRET_CODE;

import java.util.ArrayList;
import java.util.List;

public class GameController extends Pane {

    private AnimationTimer timer;
    private final GraphicsContext gc;

    // === MODEL ===
    private final GameWorld world = new GameWorld();

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    // === SYSTEMS ===
    private final List<Updatable> updatables = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();

    private final UIManager uiManager = new UIManager();
    private final DialogueBox dialogueBox = new DialogueBox();

    private final MapController mapManager = new MapController();
    private DialogueSystem dialogueSystem;
    private ParallaxSystem parallaxSystem;
    private LoadTransition loadTransition;
    private final TransitionEffect transitionEffect = new TransitionEffect(GameVar.TRANSITION_DURATION);
    private CheatTable cheatTable;
    private PauseTable pauseTable;

    private Runnable onBackToMenu;


    // === SLOT & NEW GAME FLAGS ===
    private int currentSlotNumber = -1;
    private boolean isNewGame = true;

    private boolean inGame = true;
    private final StringBuilder keySequence = new StringBuilder();

    // -------------------------------------------------------------------------
    //  Setters for Slot & Game Type
    // -------------------------------------------------------------------------

    public void setCurrentSlot(int slotNumber) {
        this.currentSlotNumber = slotNumber;
        log.info("Current Slot: {}", slotNumber);
    }

    public void setNewGame(boolean isNewGame) {
        this.isNewGame = isNewGame;
        log.info("Is New Game: {}", isNewGame);
    }

    // -------------------------------------------------------------------------
    //  Constructor
    // -------------------------------------------------------------------------

    public GameController() {
        setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        Canvas canvas = new Canvas(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setFocusTraversable(true);
        requestFocus();
    }

    // -------------------------------------------------------------------------
    //  Game Initialization
    // -------------------------------------------------------------------------

    public void initGame() {
        // --- Create base entities (Model layer) ---
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);
        world.setPaddle(paddle);
        world.setBall(ball);

        // --- Create core systems (Controller layer) ---
        BallSystem ballSystem = new BallSystem(ball, paddle);
        PaddleSystem paddleSystem = new PaddleSystem(paddle);
        PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, world.getPowerUps());

        world.setPowerUpSystem(powerUpSystem);

        Renderer renderer = new Renderer(world);
        LoadLevel loadLevel = new LoadLevel(mapManager, world, renderer);

        String dialoguePath = isNewGame ? "/Dialogue/intro.txt" : "/Dialogue/continue.txt";
        dialogueSystem = new DialogueSystem(dialoguePath, dialogueBox);
        loadTransition = new LoadTransition(world, transitionEffect, loadLevel, dialogueSystem);

        // --- Load map and build bricks/walls ---
        // --- Đã chia làm 2 dựa trên flag isNewGame
        BrickSystem brickSystem;
        if (isNewGame) {
            // NEW GAME: Load level với transition đầy đủ
            loadLevel(world.getCurrentLevel());
            brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
        } else {
            // LOAD GAME: Load map structure trước, sau đó apply state
            loadGame(); // Apply saved state lên map đã load
            renderer.setReveal(isNewGame);
            loadLevel.loadForSavedGame(world.getCurrentLevel()); // Chỉ load walls + brick structure
            brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
        }

        // --- Create managers (controllers) ---
        CollisionController collisionManager = new CollisionController(world, ballSystem, brickSystem, powerUpSystem);

        // --- Register update systems ---
        updatables.addAll(List.of(
                paddleSystem,
                ballSystem,
                powerUpSystem,
                brickSystem,
                collisionManager
        ));

        renderables.add(renderer);                                               // then the world
        renderables.add((gc) -> uiManager.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT)); // UI last

        // --- Setup parallax for level 2 ---
        if (world.getCurrentLevel() == GameVar.START_LEVEL) {
            initParallax();
        }

        //set up PauseTable o day;
        setupPauseTable();

        Sound.getInstance().playRandomMusic();

        setupKeyHandling();
        uiManager.add(dialogueBox);
        dialogueSystem.start();

        loop();
    }

    // -------------------------------------------------------------------------
    //  Level Management
    // -------------------------------------------------------------------------

    private void loadLevel(int level) { // TODO: put this in map
        loadTransition.startLevel(level);
    }

    public void loadNextLevel() {
        int currentLevel = world.getCurrentLevel();
        int nextLevel = currentLevel + 1;

        if (nextLevel > GameVar.MAX_LEVEL) {
            nextLevel = GameVar.MIN_LEVEL;
        }

        world.setCurrentLevel(nextLevel);
        loadLevel(nextLevel);
    }

    public void loadPreviousLevel() {
        int currentLevel = world.getCurrentLevel();
        int prevLevel = currentLevel - 1;

        if (prevLevel < GameVar.MIN_LEVEL) {
            prevLevel = GameVar.MAX_LEVEL;
        }

        world.setCurrentLevel(prevLevel);
        loadLevel(prevLevel);
    }

    // -------------------------------------------------------------------------
    //  Save/Load with Repository
    // -------------------------------------------------------------------------

    public void saveGame() {
        log.info("Bắt đầu lưu game...");
        GameState gameState = new GameState(world);
        log.info("=> Chuẩn bị lưu - Nhạc: '{}', Thời gian: {} ms",
                gameState.getCurrentTrackName(),
                String.format("%.0f", gameState.getCurrentTrackTime()));

        SaveDataRepository repository = new SaveDataRepository();
        repository.saveSlot(currentSlotNumber, gameState);
        log.info("Save complete!");
    }


    private void loadGame() {
        log.info("Loading from slot {}...", currentSlotNumber);
        SaveDataRepository repository = new SaveDataRepository();
        GameState loadedState = repository.loadSlot(currentSlotNumber);

        if (loadedState != null) {
            log.info("=> Đã tải từ file - Nhạc: '{}', Thời gian: {} ms",
                    loadedState.getCurrentTrackName(),
                    String.format("%.0f", loadedState.getCurrentTrackTime()));
            applyState(loadedState);
            log.info("Tải game thành công!");
        } else {
            log.info("Không có file lưu hoặc file lỗi.");
        }
    }


    public void applyState(GameState loadedState) {
        // SECTION 1: Setup Level
        world.setCurrentLevel(loadedState.getCurrentLevel());
        Sound.getInstance().playMusic(loadedState.getCurrentTrackName(), loadedState.getCurrentTrackTime());

        // SECTION 2: Apply Entity States
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

        // SECTION 3: Apply Relationships
        if (ball.isStuck()) {
            ball.alignWithPaddle(GameVar.BALL_OFFSET_Y, GameVar.BALL_ALIGN_LERP_FACTOR);
        }

        // Falling Power-Ups
        world.getPowerUps().clear();
        for (PowerUpData powerUpData : loadedState.getPowerUpsData()) {
            PowerUp p = ThePool.PowerUpPool.acquire(powerUpData.getType());
            p.setPosition(powerUpData.getX(), powerUpData.getY());
            p.setVisible(powerUpData.isVisible());
            world.getPowerUps().add(p);
        }

        // Active Power-ups
        PowerUpSystem currentPowerUpSystem = world.getPowerUpSystem();
        if (currentPowerUpSystem != null) {
            currentPowerUpSystem.reset();

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

    private void initParallax() {
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

                if (deltaTime > GameVar.MAX_DELTA_TIME) deltaTime = GameVar.MAX_DELTA_TIME;

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


    private void gameFinished() { // TODO: cheat menu uses this to trigger win condition
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Verdana", 18));
        if (!uiManager.contains(dialogueBox)) uiManager.add(dialogueBox);
        dialogueBox.start(new DialogueBox.DialogueLine[]{
                new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.EGG, "Round two? Let's go!"),
                new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.BALL, "Bring it on!")
        });
    }

    private void stopGame() { // TODO: pause uses this
        inGame = false;
        if (timer != null) timer.stop();
        Sound.getInstance().pauseMusic();
        Sound.getInstance().stopAllSounds();
        EffectRenderer.getInstance().clear();
    }

    public void resumeGame() {
        if (!inGame) {
            inGame = true;
            if (timer != null) timer.start();
            if (dialogueBox.isActive() && dialogueBox.isLineInProgress()) {
                dialogueBox.resumeDialogue();
            }
            Sound.getInstance().resumeMusic();
            log.info("Game resumed");
        }
    }


    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void backToMenu() {
        onBackToMenu.run();
    }

    private void setupKeyHandling() {
        setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == null) return;

            if (handlePauseKey(code)) {
                e.consume();
                return;
            }

            if (handleEasterEggKey(code)) {
                e.consume();
            }
        });
    }

    private boolean handlePauseKey(KeyCode code) {
        if (code == KeyCode.F1) {
            pauseTable.show();
            stopGame();
            return true;

        }
        return false;
    }

    private boolean handleEasterEggKey(KeyCode code) {
        if (code == KeyCode.BACK_QUOTE) {
            if (cheatTable != null) {
                cheatTable.show();
            } else {
                log.info("(Cheat menu not unlocked yet)");
            }
            return true;
        }
        String key = code.getName().toUpperCase();
        log.info(key);
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
            return true;
        }
        return false;
    }

    private void setupPauseTable() {
        pauseTable = new PauseTable(this);
        uiManager.add(pauseTable);
        StackPane view = pauseTable.getView();
        view.prefWidthProperty().bind(widthProperty());
        view.prefHeightProperty().bind(heightProperty());
        getChildren().add(view);
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