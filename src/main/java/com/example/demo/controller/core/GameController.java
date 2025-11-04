package com.example.demo.controller.core;

import com.example.demo.controller.map.MapController;
import com.example.demo.engine.*;
import com.example.demo.model.core.*;
import com.example.demo.model.core.effects.TransitionEffect;
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

    private final MapController mapManager = new MapController();
    private LoadTransition loadTransition;
    private final TransitionEffect transitionEffect = new TransitionEffect(GameVar.TRANSITION_DURATION);
    private DialogueSystem dialogueSystem;

    private int currentSlotNumber = -1;
    private boolean isNewGame = true;
    private boolean inGame = true;

    private Runnable onBackToMenu;

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

        if (dialogueSystem == null) {
            String dialoguePath = isNewGame ? "/Dialogue/intro.txt" : "/Dialogue/continue.txt";
            dialogueSystem = new DialogueSystem(dialoguePath, view.getUiView().getDialogueBox());
        }

        loadTransition = new LoadTransition(world, transitionEffect, loadLevel, dialogueSystem, view, this);

        if (isNewGame) {
            loadLevel.load(world.getCurrentLevel());
        } else {
            loadGame();
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
        loadTransition.startLevel(world.getCurrentLevel());
        loop();
    }
    public void loadLevel(int level) {
        world.setCurrentLevel(level);
        loadTransition.startLevel(level);
    }

    public void loadNextLevel() {
        int nextLevel = world.getCurrentLevel() + 1;
        if (nextLevel > GameVar.MAX_LEVEL) nextLevel = GameVar.MIN_LEVEL;
        loadLevel(nextLevel);
    }

    public void loadPreviousLevel() {
        int prevLevel = world.getCurrentLevel() - 1;
        if (prevLevel < GameVar.MIN_LEVEL) prevLevel = GameVar.MAX_LEVEL;
        loadLevel(prevLevel);
    }

    public void saveGame() {
        log.info("Saving game...");
        GameState state = new GameState(world);
        new SaveDataRepository().saveSlot(currentSlotNumber, state);
        log.info("Save complete.");
    }

    private void loadGame() {
        log.info("Loading from slot {}...", currentSlotNumber);
        SaveDataRepository repo = new SaveDataRepository();
        GameState loaded = repo.loadSlot(currentSlotNumber);

        if (loaded != null) {
            applyState(loaded);
            log.info("Loaded successfully!");
        } else {
            log.warn("No valid save found.");
        }
    }

    public void applyState(GameState loadedState) {
        world.applyState(loadedState);
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
        world.update(deltaTime);
        view.update(deltaTime);
        if (!view.getUiView().hasActiveUI()) {
            inputGame.update();
        }
    }

    public void resumeGame() {
        if (!inGame) {
            inGame = true;
            if (timer != null) timer.start();
            view.getUiView().getDialogueBox().resumeDialogue();
            Sound.getInstance().resumeMusic();
            log.info("Game resumed");
        }
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

    public GameWorld getWorld() {
        return world;
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
        if (dialogueSystem != null) {
            dialogueSystem.start();
        }
    }
}
