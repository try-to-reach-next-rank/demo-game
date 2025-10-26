package com.example.demo.controller.map;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.Brick;
import com.example.demo.model.map.MapData;
import com.example.demo.view.Renderer;

public class LoadLevel {
    private final MapManager mapManager;
    private final GameWorld world;
    private final Renderer renderer;

    public LoadLevel(MapManager mapManager, GameWorld world, Renderer renderer) {
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
}
