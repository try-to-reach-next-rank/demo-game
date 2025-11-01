package com.example.demo.view;

import com.example.demo.controller.map.MapController;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.Brick;
import com.example.demo.model.map.MapData;

public class LoadLevel {
    private final MapController mapManager;
    private final GameWorld world;
    private final Renderer renderer;

    public LoadLevel(MapController mapManager, GameWorld world, Renderer renderer) {
        this.mapManager = mapManager;
        this.world = world;
        this.renderer = renderer;
    }

    public MapData load(int level) {
        MapData mapData = mapManager.loadMap(level);

        world.getWalls().clear();
        world.getWalls().addAll(mapData.getWalls());
        world.setBricks(mapData.getBricks().toArray(new Brick[0]));
        world.resetForNewLevel();
        renderer.reset();

        return  mapData;
    }

    // chỉ load level, không reset render
    public void loadForSavedGame(int level) {
        MapData mapData = mapManager.loadMap(level);

        world.getWalls().clear();
        world.getWalls().addAll(mapData.getWalls());
        world.setBricks(mapData.getBricks().toArray(new Brick[0]));
    }
}
