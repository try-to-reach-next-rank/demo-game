package com.example.demo.controller.system;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;

public class SystemManager implements Updatable {
    private final GameWorld world;
    private final List<Updatable> updatables = new ArrayList<>();

    SystemManager(GameWorld world) {
        this.world = new GameWorld();
        registerAllUpdatable();
    }

    @Override
    public void update(double deltaTime) {
        for (Updatable u : updatables) {
            u.update(deltaTime);
        }
    }

    private void registerAllUpdatable() {
        registerUpdatable(new BallSystem(world.getBall(), world.getPaddle()));
        registerUpdatable(new BrickSystem(world.getBricks(), world.getPowerUps()));
        registerUpdatable(new PaddleSystem(world.getPaddle()));
        // registerUpdatable(new ParallaxSystem(world, 0, 0, null));
        registerUpdatable(new PortalSystem(world.getPortalFactory()));
        registerUpdatable(new PowerUpSystem(world.getBall(), world.getPaddle(), world.getPowerUps()));
    }

    private void registerUpdatable(Updatable system) {
        if (!updatables.contains(system)) {
            updatables.add(system);
        }
    }
}
