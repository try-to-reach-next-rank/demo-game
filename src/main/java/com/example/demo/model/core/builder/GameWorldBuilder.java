package com.example.demo.model.core.builder;

import com.example.demo.controller.core.CollisionController;
import com.example.demo.controller.system.SystemManager;
import com.example.demo.model.core.entities.Brick;
import com.example.demo.model.core.factory.EntityFactory;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.map.MapData;

import java.util.List;

public class GameWorldBuilder {
    private final GameWorld world = new GameWorld();

    public GameWorldBuilder withMap(MapData mapData) {
        world.getWalls().clear();
        world.getWalls().addAll(mapData.walls());
        world.setBricks(mapData.bricks().toArray(new Brick[0]));
        return this;
    }

    public GameWorldBuilder withLevel(int level) {
        world.setCurrentLevel(level);
        return this;
    }

    public GameWorldBuilder withEntities() {
        world.setPaddle(EntityFactory.createPaddle());
        world.setBall(EntityFactory.createBall(world.getPaddle()));
        return this;
    }

    public GameWorldBuilder withSystems() {
        // TODO: FOR PU SYS
        // world.setPowerUpSystem(powerUpSystem);
        return this;
    }

    public GameWorld build() {
        return world;
    }
}
