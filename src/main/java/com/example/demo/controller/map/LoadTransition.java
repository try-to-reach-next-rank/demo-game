package com.example.demo.controller.map;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.effects.TransitionEffect;
import com.example.demo.model.map.MapData;
import com.example.demo.model.system.BrickSystem;
import com.example.demo.model.utils.dialogue.DialogueSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadTransition {
    private final GameWorld world;
    private final TransitionEffect transitionEffect;
    private final LoadLevel levelLoader;
    private final DialogueSystem dialogueSystem;
    private final Logger log = LoggerFactory.getLogger(LoadTransition.class);

    private boolean inGame;
    private BrickSystem brickSystem;

    public LoadTransition(
            GameWorld world, TransitionEffect transitionEffect,
            LoadLevel levelLoader,
            DialogueSystem dialogueSystem
    ) {
        this.world = world;
        this.transitionEffect = transitionEffect;
        this.levelLoader = levelLoader;
        this.dialogueSystem = dialogueSystem;
    }

    public void startLevel(int level) {
        inGame = false;
        transitionEffect.start(
                () -> {
                    MapData mapData = levelLoader.load(level);
                    brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
                },
                () -> {
                    inGame = true;
                    MapData mapData = levelLoader.load(level);
                    log.info("Loaded map: {} with {} bricks", level, mapData.getBricks().size());
                    dialogueSystem.start();
                }
        );
    }
}
