package com.example.demo.view;

import com.example.demo.controller.core.CollisionController;
import com.example.demo.controller.core.GameController;
import com.example.demo.controller.core.entities.PortalController;
import com.example.demo.controller.system.*;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.effects.TransitionEffect;
import com.example.demo.model.map.MapData;
import com.example.demo.utils.dialogue.DialogueSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoadTransition {
    private final GameWorld world;
    private final TransitionEffect transitionEffect;
    private final LoadLevel levelLoader;
    private final DialogueSystem dialogueSystem;
    private final GameView gameView;
    private final GameController controller;
    private final Logger log = LoggerFactory.getLogger(LoadTransition.class);

    public LoadTransition(GameWorld world, TransitionEffect transitionEffect,
                          LoadLevel levelLoader, DialogueSystem dialogueSystem,
                          GameView gameView, GameController controller) {
        this.world = world;
        this.transitionEffect = transitionEffect;
        this.levelLoader = levelLoader;
        this.dialogueSystem = dialogueSystem;
        this.gameView = gameView;
        this.controller = controller;

        gameView.setTransitionEffect(transitionEffect);
    }

    /** Start level with transition */
    public void startLevel(int level) {
        controller.setInGame(false);

        transitionEffect.start(
                // midpoint callback: load level & register systems
                () -> {
                    gameView.getCoreView().reset();
                    MapData mapData = levelLoader.load(level);
                    
                    // TODO: JUST CLEAR WORLD
                    world.clearUpdatables();

                    BallSystem ballSystem = new BallSystem(world.getBall(), world.getPaddle());
                    PaddleSystem paddleSystem = new PaddleSystem(world.getPaddle());
                    PowerUpSystem powerUpSystem = world.getPowerUpSystem();
                    BrickSystem brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
                    CollisionController collisionController = new CollisionController(world, ballSystem, brickSystem, powerUpSystem);

                    // TODO: IMPLEMENT A NEW CLASS FOR UPDATE ALL System, Controller 
                    world.clearUpdatables();
                    List.of(ballSystem, paddleSystem, powerUpSystem, brickSystem, collisionController)
                            .forEach(world::registerUpdatable);

                    // Reset view/UI
                    gameView.reset();

                    log.info("Loaded level {} with {} bricks", level, mapData.getBricks().size());
                },
                // end callback: re-enable game
                () -> controller.setInGame(true)
        );
    }
}
