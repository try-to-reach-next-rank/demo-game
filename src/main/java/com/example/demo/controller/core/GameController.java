package com.example.demo.controller.core;

import com.example.demo.controller.map.MapController;
import com.example.demo.controller.system.SystemManager;
import com.example.demo.engine.*;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.factory.GameFactory;
import com.example.demo.model.map.MapData;
import com.example.demo.model.menu.AchievementModel;
import com.example.demo.model.state.*;
import com.example.demo.model.state.highscore.HighScoreState;
import com.example.demo.utils.BrickFactoryUtil;
import com.example.demo.utils.Input;
import com.example.demo.utils.Sound;
import com.example.demo.utils.dialogue.DialogueSystem;
import com.example.demo.utils.var.GameVar;
import com.example.demo.utils.var.GlobalVar;
import com.example.demo.view.*;
import javafx.animation.AnimationTimer;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameController extends Pane {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private GameWorld world;
    private final SystemManager systemManager;
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
    private GameState loadedState = null;

    public GameController() {
        setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);

        if (isNewGame) {
            world = GameFactory.createNewGame(mapManager);
            world.setCurrentScore(0);
        } else {
            GameState loaded = saveController.loadGame(currentSlotNumber);
            world = GameFactory.loadFromState(loaded, mapManager);
        }

        systemManager = new SystemManager(world);
        view = new GameView(world, this);
        getChildren().add(view);

        setFocusTraversable(true);
        requestFocus();
        setupKeyHandling();
    }

    public void initGame() {
        world.startPlayTimer();
        String dialoguePath = isNewGame ? "/Dialogue/intro.txt" : "/Dialogue/continue.txt";
        view.getUiView().loadDialogue(dialoguePath);
        inputGame = new Input(world.getPaddle(), world.getBall());

        // Init parallax everymap -> fix bugs if use cheatable
        view.getCoreView().initParallax();

        Sound.getInstance().playRandomMusic();

        view.startTransition(
                () -> setupLevel(world.getCurrentLevel()),
                () -> setInGame(true)
        );
        // BrickSystem brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());

        loop();
    }

    // ========== Level Management -> Delegate to MapController ==========
    private void setupLevel(int level) {
        MapData mapData = mapManager.loadMap(level);

        // Clear and add new map content
        world.getWalls().clear();
        world.getWalls().addAll(mapData.walls());

        Brick[] bricksToLoad;
        if (loadedState != null && !loadedState.getBricksData().isEmpty()) {
            // Rebuild bricks from saved BrickData
            bricksToLoad = new Brick[loadedState.getBricksData().size()];
            for (BrickData data : loadedState.getBricksData()) {
                bricksToLoad[data.getId()] = BrickFactoryUtil.createBrickFromData(data);
            }
            loadedState = null; // only use once
        } else {
            bricksToLoad = mapData.bricks().toArray(new Brick[0]);
        }
        world.setBricks(bricksToLoad);
        /*
        // Reset ball và powerup khi load level mới
        if (world.getBall() != null) {
            world.getBall().resetState();
        }
        if (world.getPowerUpSystem() != null) {
            world.getPowerUpSystem().reset();
        }
        */


        // Reset any level-specific state in systems
        systemManager.clear();
        
        // Reset view
        view.getCoreView().setLevelLoaded(true);
        view.reset();
        view.getCoreView().reset();

        // Thiết lập callback để cộng điểm
        // brickSystem.setOnBrickDestroyed(brick -> {
        //     double centerX = brick.getX() + brick.getWidth() / 2;
        //     double centerY = brick.getY() + brick.getHeight() / 2;
        //     world.addScore(brick.getScoreValue(), centerX, centerY);
//            log.info("Brick destroyed! +{} points | Total score: {}",
//                    brick.getScoreValue(), world.getCurrentScore());
        // });
        log.info("Loaded level {}", level);
    }

    public void loadLevel(int level) {
        world.setCurrentLevel(level);
        unlockLevelAchievement(world.getCurrentLevel());
        AchievementDialogue(world.getCurrentLevel());

        System.out.println("Loaded level " + level);


        view.startTransition(
                () -> setupLevel(level),
                () -> {
                    setInGame(true);
                    startIntroDialogue();
                }
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
        this.loadedState = loadedState;
        world.applyState(loadedState);
    }

    // ========== Auto Level Progression ==========
    private boolean checkLevelCompletion() {
        if (world.getCurrentScore() > world.getHighScore()) {
            world.setHighScore(world.getCurrentScore());
            log.info("Updated high score to: {}", world.getHighScore());
        }
        if (world.getRemainingBricksCount() > 0) {
            return false;
        }



        if(world.getCurrentLevel() < 3) {
            log.info("Level complete!");
            unlockLevelAchievement(world.getCurrentLevel());
            System.out.println(world.getCurrentLevel());
            saveController.saveGame(world, currentSlotNumber);
            //loadNextLevel();
        }else{
            log.info("Game  complete!");
            unlockLevelAchievement(4);
            saveController.saveGame(world, currentSlotNumber);
        }
        return true;
    }

    private void unlockLevelAchievement(int levelNumber) {
        try {
            HighScoreState highScoreState = HighScoreState.getInstance();
            AchievementModel achievementModel = new AchievementModel(highScoreState);

            // Unlock theo level
            if (levelNumber == 2) {
                achievementModel.unlockWinLevel1();
                log.info("🏆 Unlocked achievement: Win Level 1");
            } else if (levelNumber  == 3) {
                achievementModel.unlockWinLevel2();
                log.info("🏆 Unlocked achievement: Win Level 2");
            }
            if(levelNumber == 4){
                achievementModel.unlockWinGame();
                log.info("🏆 Unlocked achievement: Win Game");
            }

        } catch (Exception e) {
            log.error("Failed to unlock achievement for level {}: {}", levelNumber, e.getMessage());
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
                    //log.info("FPS: {}  {}", frames, world.getCurrentLevel());
                    fpsTimer = 0;
                    frames = 0;
                }
            }
        };
        timer.start();
    }

    private void update(double deltaTime) {
        if (!paused) {
            systemManager.update(deltaTime);

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
        world.pausePlayTimer();
    }

    public void resumeGame() {
        paused = false;
        world.resumePlayTimer();
        if (timer != null) timer.start();
        view.getUiView().resumeDialogue();
        Sound.getInstance().resumeMusic();
        log.info("Game resumed");
    }

    public void onKeyPressed(KeyCode code) {
        inputGame.handleKeyPressed(code);
        if (code == KeyCode.H) {
            view.getCoreView().triggerHandGrab();
        }
    }

    public void onKeyReleased(KeyCode code) {
        inputGame.handleKeyReleased(code);
    }

    private void setupKeyHandling() {
        setOnKeyPressed(view::handleKeyPressed);
        setOnKeyReleased(view::handleKeyReleased);
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

    public void AchievementDialogue(int levelNumber) {
        String dialoguePath =  "/Dialogue/" + levelNumber+ ".txt";
        System.out.println(dialoguePath);
        view.getUiView().loadDialogue(dialoguePath);
    }

    public SystemManager getSystemManager() {
        return this.systemManager;
    }

    public boolean GetIsNewGame() {
        System.out.println(isNewGame);
        return this.isNewGame;
    }
}
