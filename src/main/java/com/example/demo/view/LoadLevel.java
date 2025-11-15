package com.example.demo.view;

import com.example.demo.controller.map.MapController;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.map.MapData;

public class LoadLevel {
    private final MapController mapManager;
    private final GameWorld world;
    private final GameView gameView;

    public LoadLevel(MapController mapManager, GameWorld world, GameView gameView) {
        this.mapManager = mapManager;
        this.world = world;
        this.gameView = gameView;
    }

    public MapData load(int level) {
        MapData mapData = mapManager.loadMap(level);

        world.getWalls().clear();
        world.getWalls().addAll(mapData.walls());
        world.setBricks(mapData.bricks().toArray(new Brick[0]));
        world.resetForNewLevel();

        gameView.reset();

        return mapData;
    }

    public void loadForSavedGame(int level) {
        MapData mapData = mapManager.loadMap(level);

        world.getWalls().clear();
        world.getWalls().addAll(mapData.walls());
        world.setBricks(mapData.bricks().toArray(new Brick[0]));
    }
}
