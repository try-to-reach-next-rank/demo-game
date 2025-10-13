package com.example.demo.controller;

import com.example.demo.engine.*;
import com.example.demo.model.core.*;
import com.example.demo.model.core.bricks.Brick;
import com.example.demo.model.states.MapData;
import com.example.demo.model.utils.GlobalVar;
import com.example.demo.model.utils.Sound;
import com.example.demo.system.*;
import com.example.demo.view.*;
import com.example.demo.view.graphics.ParallaxLayer;
import com.example.demo.view.ui.DialogueBox;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private final Random random = new Random();

    private final MapManager mapManager = new MapManager();
    private CameraManager cameraManager;
    private CollisionManager collisionManager;
    private BrickSystem brickSystem;
    private DialogueSystem dialogueSystem; /** new dialogue system for running dialogue through txt*/
    private final RenderManager renderManager = new RenderManager(GlobalVar.WIDTH, GlobalVar.HEIGHT);
    private final List<ParallaxLayer> parallaxLayers = new ArrayList<>();

    private boolean inGame = true;
    private final StringBuilder keySequence = new StringBuilder();
    private static final String SECRET_CODE = "PHUC";

    public GameManager() {
        setPrefSize(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        Canvas canvas = new Canvas(GlobalVar.WIDTH, GlobalVar.HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        setFocusTraversable(true);
        requestFocus();

        // Lấy các lệnh từ intro.txt rồi gọi dialogue tương ứng thông qua dialogueBox
        dialogueSystem = new DialogueSystem("/dialogue/intro.txt", dialogueBox);
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
        PowerUpSystem powerUpSystem = new PowerUpSystem(ball, paddle, world.getPowerUps());

        // --- Load map and build bricks/walls ---
        loadLevel(world.getCurrentLevel());
        brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());

        // --- Create managers (controllers) ---
        cameraManager = new CameraManager(world, parallaxLayers, 0.15, 8.0,
                new double[]{1.0, 0.6, 0.35, 0.2});
        collisionManager = new CollisionManager(world, ballSystem, brickSystem, powerUpSystem);

        // --- Register update systems ---
        updatables.addAll(List.of(
                paddleSystem,
                ballSystem,
                powerUpSystem,
                brickSystem,
                collisionManager,
                cameraManager
        ));

        // --- Register renderables (View layer) ---
        Renderer renderer = new Renderer(world);
        renderables.add((gc) -> renderManager.drawParallax(gc, parallaxLayers)); // background first
        renderables.add(renderer);                                               // then the world
        renderables.add((gc) -> uiManager.render(gc, GlobalVar.WIDTH, GlobalVar.HEIGHT)); // UI last

        // --- Setup parallax for first level ---
        if (world.getCurrentLevel() == 1) initParallax();

        // --- INTRO ---

        dialogueSystem.start();
        Sound.getInstance().playRandomMusic();
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
    //  Parallax Setup
    // -------------------------------------------------------------------------

    private void initParallax() {
        ParallaxLayer l1 = new ParallaxLayer("/images/layer1.png", 0.25);
        ParallaxLayer l2 = new ParallaxLayer("/images/layer2.png", 0.50);
        ParallaxLayer l3 = new ParallaxLayer("/images/layer3.png", 0.75);
        ParallaxLayer l4 = new ParallaxLayer("/images/layer4.png", 1.00);

        l1.setWrapWidth(GlobalVar.WIDTH * 1.5);
        l2.setWrapWidth(GlobalVar.WIDTH * 1.6);
        l3.setWrapWidth(GlobalVar.WIDTH * 1.7);
        l4.setWrapWidth(GlobalVar.WIDTH * 1.8);

        parallaxLayers.addAll(List.of(l1, l2, l3, l4));
    }

    // -------------------------------------------------------------------------
    //  Game Loop
    // -------------------------------------------------------------------------

    private void loop() {
        final double FPS = GlobalVar.FPS;
        final double UPDATE_INTERVAL = 1e9 / FPS;
        final long[] lastUpdate = {System.nanoTime()};

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (now - lastUpdate[0] >= UPDATE_INTERVAL) {
                    update(1.0 / FPS);
                    lastUpdate[0] += (long) UPDATE_INTERVAL;
                }
                render();
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

        for (Updatable system : updatables) system.update(deltaTime);
        EffectRenderer.getInstance().update(deltaTime);
    }

    private void render() {
        gc.clearRect(0, 0, GlobalVar.WIDTH, GlobalVar.HEIGHT);
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

    public Paddle getPaddle() { return world.getPaddle(); }
    public Ball getBall() { return world.getBall(); }
    public UIManager getUIManager() { return uiManager; }
}
