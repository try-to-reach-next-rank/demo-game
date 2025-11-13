package com.example.demo.model.core.builder;

import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.ThePool;
import com.example.demo.model.core.factory.EntityFactory;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.map.MapData;
import com.example.demo.utils.GameStateRestore;

import java.util.List;

public class GameWorldBuilder {
    private final GameWorld world = new GameWorld();

    public GameWorldBuilder withMap(MapData mapData) {
        world.getWalls().clear();
        world.getWalls().addAll(mapData.walls());

        List<Brick> brickList = mapData.bricks();
        world.setBricks(brickList.toArray(new Brick[0]));
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

    // public GameWorldBuilder withSystems() {
    //     BallSystem ballSystem = new BallSystem(world.getBall(), world.getPaddle());
    //     PaddleSystem paddleSystem = new PaddleSystem(world.getPaddle());
    //     PowerUpSystem powerUpSystem = new PowerUpSystem(world.getBall(), world.getPaddle(), world.getPowerUps());
    //     BrickSystem brickSystem = new BrickSystem(world.getBricks(), world.getPowerUps());
    //     CollisionController collisionManager = new CollisionController(world, ballSystem, brickSystem, powerUpSystem);

    //     world.setPowerUpSystem(powerUpSystem);

    //     // Register systems for updating
    //     List.of(ballSystem, paddleSystem, brickSystem, powerUpSystem, collisionManager)
    //             .forEach(world::registerUpdatable);

    //     return this;
    // }

    public GameWorldBuilder withGameStateRestore() {
        ThePool pool = new ThePool(); // hoặc inject vào nếu cần
        world.setGameStateRestore(new GameStateRestore(pool));
        return this;
    }

    public GameWorld build() {
        return world;
    }
}
