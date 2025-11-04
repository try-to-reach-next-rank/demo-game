package com.example.demo.controller.core;

import com.example.demo.controller.map.MapController;
import com.example.demo.engine.*;
import com.example.demo.model.core.*;
import com.example.demo.model.core.effects.TransitionEffect;
import com.example.demo.model.map.MapData;
import com.example.demo.model.state.*;
import com.example.demo.model.system.*;

import com.example.demo.utils.Input;
import com.example.demo.utils.Sound;
import com.example.demo.repository.SaveDataRepository;
import com.example.demo.utils.dialogue.DialogueSystem;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.*;
import javafx.animation.AnimationTimer;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameController extends Pane {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final GameWorld world = new GameWorld();
    private final GameView view;
    private AnimationTimer timer;
    private Input inputGame;
    // === CONTROLLERS ===
    private final MapController mapManager = new MapController();
    private final SaveController saveController = new SaveController();

    private int currentSlotNumber = -1;
    private boolean isNewGame = true;
    private boolean inGame = true;
    private boolean paused = false;

    private Runnable onBackToMenu;

    // ========== NEW: Level Completion Check Throttling ==========
    private static final double LEVEL_CHECK_INTERVAL = 3.0; // Check every 3 seconds
    private double levelCheckTimer = 0.0;

    public GameController() {
        setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        view = new GameView(world, this);
        getChildren().add(view);

        setFocusTraversable(true);
        requestFocus();

        setupKeyHandling();
    }

    public void initGame() {
        world.init();

        BallSystem ballSystem = new BallSystem(world.getBall(), world.getPaddle());
        PaddleSystem paddleSystem = new PaddleSystem(world.getPaddle());
        PowerUpSystem powerUpSystem = new PowerUpSystem(world.getBall(), world.getPaddle(), world.getPowerUps());
        world.setPowerUpSystem(powerUpSystem);

        LoadLevel loadLevel = new LoadLevel(mapManager, world, view);

        String dialoguePath = isNewGame ? "/Dialogue/intro.txt" : "/Dialogue/continue.txt";
        view.getUiView().loadDialogue(dialoguePath);

        if (isNewGame) {
            loadLevel.load(world.getCurrentLevel());
        } else {
            GameState loaded = saveController.loadGame(currentSlotNumber);
            if (loaded != null) {
                world.applyState(loaded);
            }
            loadLevel.loadForSavedGame(world.getCurrentLevel());
        }

        BrickSystem brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());

        CollisionController collisionManager = new CollisionController(world, ballSystem, brickSystem, powerUpSystem);

        world.clearUpdatables();
        List.of(ballSystem, paddleSystem, powerUpSystem, brickSystem, collisionManager)
                        .forEach(world::registerUpdatable);

        if (world.getCurrentLevel() == GameVar.START_LEVEL) {
            view.getCoreView().initParallax();
        }

        Sound.getInstance().playRandomMusic();

        inputGame = new Input(world.getPaddle(), world.getBall());
        setupKeyHandling();
        view.startTransition(
                () -> setupLevel(world.getCurrentLevel()),
                () -> setInGame(true)
        );
        loop();
    }

    // ========== Level Management -> Delegate to MapController ==========
    private void setupLevel(int level) {
        view.getCoreView().reset();
        MapData mapData = new LoadLevel(mapManager, world, view).load(level);
        world.clearUpdatables();

        BallSystem ballSystem = new BallSystem(world.getBall(), world.getPaddle());
        PaddleSystem paddleSystem = new PaddleSystem(world.getPaddle());
        PowerUpSystem powerUpSystem = world.getPowerUpSystem();
        BrickSystem brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
        CollisionController collisionManager = new CollisionController(world, ballSystem, brickSystem, powerUpSystem);

        List.of(ballSystem, paddleSystem, powerUpSystem, brickSystem, collisionManager)
                .forEach(world::registerUpdatable);

        view.reset();
        log.info("Loaded level {}", level);
    }

    public void loadLevel(int level) {
        world.setCurrentLevel(level);
        view.startTransition(
                () -> setupLevel(level),
                () -> setInGame(true)
        );
    }

    public void loadNextLevel() {
        int nextLevel = mapManager.getNextLevel(world.getCurrentLevel());
        loadLevel(nextLevel);
    }

    public void loadPreviousLevel() {
        int prevLevel = mapManager.getPreviousLevel(world.getCurrentLevel());
        loadLevel(prevLevel);
    }

    // ========== Save/Load -> Delegate to SaveController ==========

    public void saveGame() {
        saveController.saveGame(world, currentSlotNumber);  // ← CHANGED
    }

    public void applyState(GameState loadedState) {
        world.applyState(loadedState);
    }

    // ========== Auto Level Progression ==========

    private void checkLevelCompletion() {
        if (world.isLevelComplete()) {
            log.info("Level complete! Remaining bricks: {}", world.getRemainingBricksCount());
            loadNextLevel();
        }
    }

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
                view.render();

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

    private void update(double deltaTime) {
        if (!paused) {
            world.update(deltaTime);

            // ← OPTIMIZED: Only check level completion every LEVELCHECKINTERVAL seconds
            levelCheckTimer += deltaTime;
            if (levelCheckTimer >= LEVEL_CHECK_INTERVAL) {
                checkLevelCompletion();
                levelCheckTimer = 0.0; // Reset timer
            }

            if (!view.getUiView().hasActiveUI()) {
                inputGame.update();
            }
        }
        view.update(deltaTime);
    }

    public void pauseGame() {
        paused = true;
    }

    public void resumeGame() {
        paused = false;
        if (timer != null) timer.start();
        view.getUiView().resumeDialogue();
        Sound.getInstance().resumeMusic();
        log.info("Game resumed");
    }

    public void onKeyPressed(KeyCode code) {
        inputGame.handleKeyPressed(code);
    }

    public void onKeyReleased(KeyCode code) {
        inputGame.handleKeyReleased(code);
    }

    private void setupKeyHandling() {
        setOnKeyPressed(view::handleKeyPressed);
        setOnKeyReleased(view::handleKeyReleased);
    }

    public Paddle getPaddle() {
        return world.getPaddle();
    }

    public Ball getBall() {
        return world.getBall();
    }

    public void backToMenu() {
        onBackToMenu.run();
    }

    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void setCurrentSlot(int slotNumber) {
        this.currentSlotNumber = slotNumber;
        log.info("Current Slot: {}", slotNumber);
    }

    public void setNewGame(boolean isNewGame) {
        this.isNewGame = isNewGame;
        log.info("Is New Game: {}", isNewGame);
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void startIntroDialogue() {
        view.getUiView().startDialogue();
    }
}
