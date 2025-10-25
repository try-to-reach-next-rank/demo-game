package com.example.demo.controller;

import com.example.demo.engine.*;
import com.example.demo.model.core.*;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.map.MapData;
import com.example.demo.model.state.*;
import com.example.demo.model.system.*;
import com.example.demo.model.utils.GameVar;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.model.utils.GlobalVar.SECRET_CODE;

public class GameManager extends Pane {

    private AnimationTimer timer;
    private final GraphicsContext gc;

    // === MODEL ===
    private final GameWorld world = new GameWorld();

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

        dialogueSystem = new DialogueSystem("/Dialogue/intro.txt", dialogueBox);
        setupSecretCodeEasterEgg();

        uiManager.add(dialogueBox);
        initGame();
    }

    // -------------------------------------------------------------------------
    //  Game Initialization
    // -------------------------------------------------------------------------

    private void initGame() {
        // --- Load all resources ---
        AssetManager.getInstance().loadAll();

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

        // --- Load map and build bricks/walls ---
        loadLevel(world.getCurrentLevel());
        brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());

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

        // --- Register renderables (View layer) ---
        Renderer renderer = new Renderer(world);
        renderables.add(renderer);
        renderables.add((gc) -> uiManager.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT));

        // --- Setup parallax for level 2 ---
        if (world.getCurrentLevel() == GameVar.START_LEVEL) {
            initParallax();
        }

        Sound.getInstance().playRandomMusic();

        // --- LOAD GAME if not New Game ---
        if (!isNewGame) {
            loadGame();
        }

        // --- INTRO DIALOGUE only for New Game ---
        if (isNewGame) {
            dialogueSystem.start();
        }

        loop();
    }

    // -------------------------------------------------------------------------
    //  Level Management
    // -------------------------------------------------------------------------

    private void loadLevel(int level) {
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
    }

    // -------------------------------------------------------------------------
    //  Save/Load with Repository
    // -------------------------------------------------------------------------

    private void saveGame() {
        if (currentSlotNumber == -1) {
            System.err.println("[GameManager] No slot selected, cannot save!");
            return;
        }

        System.out.println("[GameManager] Saving to slot " + currentSlotNumber + "...");
        GameState gameState = new GameState(world);

        SaveDataRepository repository = new SaveDataRepository();
        repository.saveSlot(currentSlotNumber, gameState);
        System.out.println("[GameManager] Save complete!");
    }

    private void loadGame() {
        if (currentSlotNumber == -1) {
            System.out.println("[GameManager] No slot selected");
            return;
        }

        System.out.println("[GameManager] Loading from slot " + currentSlotNumber + "...");

        SaveDataRepository repository = new SaveDataRepository();
        GameState loadedState = repository.loadSlot(currentSlotNumber);

        if (loadedState != null) {
            applyState(loadedState);
            System.out.println("[GameManager] Load complete!");
        } else {
            System.out.println("[GameManager] No save data found in slot " + currentSlotNumber);
        }
    }

    public void applyState(GameState loadedState) {
        // SECTION 1: Setup Level
        loadLevel(loadedState.getCurrentLevel());

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
                    System.out.println("FPS: " + frames);
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
        uiManager.update(deltaTime);
        if (!inGame || uiManager.hasActiveUI()) return;

        if (parallaxSystem != null) {
            parallaxSystem.update(deltaTime);
        }

        for (Updatable system : updatables) system.update(deltaTime);
        EffectRenderer.getInstance().update(deltaTime);
    }

    private void render() {
        gc.clearRect(0, 0, GlobalVar.WIDTH, GlobalVar.HEIGHT);

        if (parallaxSystem != null) {
            parallaxSystem.render(gc);
        }

        for (Renderable r : renderables) r.render(gc);
        uiManager.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT);
    }

    // -------------------------------------------------------------------------
    //  Misc
    // -------------------------------------------------------------------------

    private void setupSecretCodeEasterEgg() {
        setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == null) return;

            // Save (F5) and Load (F9) shortcuts
            if (code == KeyCode.F5) {
                saveGame();
            }
            if (code == KeyCode.F9) {
                loadGame();
            }

            // Secret code logic
            String key = code.getName().toUpperCase();
            keySequence.append(key);
            if (keySequence.length() > SECRET_CODE.length()) {
                keySequence.delete(0, keySequence.length() - SECRET_CODE.length());
            }
            if (keySequence.toString().equals(SECRET_CODE)) {
                dialogueBox.start(new DialogueBox.DialogueLine[]{
                        new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.EGG, "You found the secret!"),
                        new DialogueBox.DialogueLine(DialogueBox.DialogueLine.Speaker.BALL, "Whoa, how did you unlock this?")
                });
                keySequence.setLength(0);
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