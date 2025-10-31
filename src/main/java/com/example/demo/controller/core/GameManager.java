package com.example.demo.controller.core;

import com.example.demo.controller.map.LoadLevel;
import com.example.demo.controller.map.LoadTransition;
import com.example.demo.controller.map.MapManager;

import com.example.demo.engine.*;
import com.example.demo.model.core.*;
import com.example.demo.model.core.effects.TransitionEffect;
import com.example.demo.model.state.*;
import com.example.demo.model.system.*;

import com.example.demo.model.utils.GameVar;

import com.example.demo.model.utils.CheatTable;
import com.example.demo.model.utils.PauseTable;

import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Sound;
import com.example.demo.model.utils.dialogue.DialogueBox;
import com.example.demo.model.utils.dialogue.DialogueSystem;
import com.example.demo.repository.SaveDataRepository;
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

import java.util.ArrayList;
import java.util.List;

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
    private DialogueSystem dialogueSystem;
    private ParallaxSystem parallaxSystem;
    private PowerUpSystem powerUpSystem;
    private Renderer renderer;
    private LoadTransition loadTransition;
    private final TransitionEffect transitionEffect = new TransitionEffect(2.0); // 1.5s duration
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
        System.out.println("[GameManager] Current Slot: " + this.currentSlotNumber);
    }

    public void setNewGame(boolean isNewGame) {
        this.isNewGame = isNewGame;
        System.out.println("[GameManager] Is New Game: " + this.isNewGame);
    }

    // -------------------------------------------------------------------------
    //  Constructor
    // -------------------------------------------------------------------------

    public GameManager() {
        setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        Canvas canvas = new Canvas(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setFocusTraversable(true);
        requestFocus();
        //initGame();
    }

    // -------------------------------------------------------------------------
    //  Game Initialization
    // -------------------------------------------------------------------------


    public void initGame() {
        // --- Load all resources ---



        // --- Create base entities (Model layer) ---
        Paddle paddle = new Paddle();
        Ball ball = new Ball(paddle);
        world.setPaddle(paddle);
        world.setBall(ball);

        // --- Create core systems (Controller layer) ---
        BallSystem ballSystem = new BallSystem(ball, paddle);
        PaddleSystem paddleSystem = new PaddleSystem(paddle);
        this.powerUpSystem = new PowerUpSystem(ball, paddle, world.getPowerUps());

        world.setPowerUpSystem(this.powerUpSystem);

        this.renderer = new Renderer(world);
        LoadLevel loadLevel = new LoadLevel(mapManager, world, renderer);
        loadTransition = new LoadTransition(world, transitionEffect, loadLevel, dialogueSystem);

        // --- Load map and build bricks/walls ---
        // --- Đã chia làm 2 dựa trên flag isNewGame
        if (isNewGame) {
            // NEW GAME: Load level với transition đầy đủ
            loadLevel(world.getCurrentLevel());
            brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
        } else {
            // LOAD GAME: Load map structure trước, sau đó apply state
            loadLevel.loadForSavedGame(world.getCurrentLevel()); // Chỉ load walls + brick structure
            brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
            loadGame(); // Apply saved state lên map đã load
        }

        // --- Create managers (controllers) ---
        collisionManager = new CollisionManager(world, ballSystem, brickSystem, powerUpSystem);

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

        Sound.getInstance().playRandomMusic();

        // --- LOAD GAME if not New Game ---
        if (isNewGame) {
            dialogueSystem = new DialogueSystem("/Dialogue/intro.txt", dialogueBox);
            setupSecretCodeEasterEgg();
            uiManager.add(dialogueBox);
            dialogueSystem.start();
        }
        else {
            dialogueSystem = new DialogueSystem("/Dialogue/continue.txt", dialogueBox);
            loadGame();
            setupSecretCodeEasterEgg();
            uiManager.add(dialogueBox);
            dialogueSystem.start();
        }

        loop();
    }

    // -------------------------------------------------------------------------
    //  Level Management
    // -------------------------------------------------------------------------


    private void loadLevel(int level) { // TODO: put this in map\
        loadTransition.startLevel(level);
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

    // -------------------------------------------------------------------------
    //  Save/Load with Repository
    // -------------------------------------------------------------------------

    public void saveGame() {

        log.info("Bắt đầu lưu game...");
        // construct gameState để thu thập toàn bộ trạng thái game

        GameState gameState = new GameState(world);

        SaveDataRepository repository = new SaveDataRepository();
        repository.saveSlot(currentSlotNumber, gameState);
        System.out.println("[GameManager] Save complete!");
    }


    private void loadGame() {

        log.info("[GameManager] Loading from slot " + currentSlotNumber + "...");

        SaveDataRepository repository = new SaveDataRepository();
        GameState loadedState = repository.loadSlot(currentSlotNumber);

        if (loadedState != null) {
            applyState(loadedState);
            log.info("Tải game thành công!");
        } else {
            log.info("Không có file lưu hoặc file lỗi.");

        }
    }


    public void applyState(GameState loadedState) {

        // không loadTransition các thứ nữa , chỉ apply State thôi , còn chỉ newGame mới load Transition
        world.setCurrentLevel(loadedState.getCurrentLevel());

        // Apply entity states từ save file
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
            ball.alignWithPaddle(10, 1.0);
        }

        // Falling Power-Ups
        world.getPowerUps().clear();
        for (PowerUpData powerUpData : loadedState.getPowerUpsData()) {
            PowerUp p = new PowerUp(powerUpData.getType());
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

        /** Phần này là pause Menu
         *
         */


        pauseTable = new PauseTable(this);

        StackPane view = pauseTable.getView();
        view.prefWidthProperty().bind(widthProperty());
        view.prefHeightProperty().bind(heightProperty());
        getChildren().add(view);
        pauseTable.hide();

        setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == null) return;

            // Phím `~` (BACK_QUOTE) bây giờ CHỈ dùng để MỞ menu
            if (code == KeyCode.BACK_QUOTE) {
                if (cheatTable != null) {
                    cheatTable.show(); // Chỉ mở
                }
                else {
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
            if(code == KeyCode.F1) {
                pauseTable.show();
                stopGame();
            }




            // Logic cho secret code
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
                return;
            }

        });
    }






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
        Sound.getInstance().stopMusic();
        EffectRenderer.getInstance().clear();
    }

    public void resumeGame() {
        if (!inGame) {
            inGame = true;
            if (timer != null) timer.start();
            Sound.getInstance().resumeMusic();
            log.info("[GameManager] Game resumed");
        }
    }


    public void setOnBackToMenu(Runnable callback) {
        this.onBackToMenu = callback;
    }

    public void backToMenu() {
        onBackToMenu.run();
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