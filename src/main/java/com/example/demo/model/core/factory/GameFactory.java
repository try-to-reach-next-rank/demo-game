package com.example.demo.model.core.factory;

import com.example.demo.controller.map.MapController;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.builder.GameWorldBuilder;
import com.example.demo.model.map.MapData;
import com.example.demo.model.state.GameState;
import com.example.demo.utils.var.GameVar;

public class GameFactory {
    public static GameWorld createNewGame(MapController mapController) {
        MapData mapData = mapController.loadMap(GameVar.START_LEVEL);
        return new GameWorldBuilder()
                .withLevel(GameVar.START_LEVEL)
                .withEntities()
                .withMap(mapData)
                .withSystems()
                .build();
    }

    public static GameWorld loadFromState(GameState state, MapController mapController) {
        MapData mapData = mapController.loadMap(state.getCurrentLevel());

        GameWorld world = new GameWorldBuilder()
                .withLevel(state.getCurrentLevel())
                .withEntities()
                .withMap(mapData)
                .withSystems()
                .build();

        world.applyState(state);
        return world;
    }
}
