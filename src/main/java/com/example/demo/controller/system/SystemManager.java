package com.example.demo.controller.system;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;

import java.util.Map;
import java.util.HashMap;

public class SystemManager implements Updatable {
    private final GameWorld world;
    private final Map<Class<? extends Updatable>, Updatable> systems = new HashMap<>();

    public SystemManager(GameWorld world) {
        this.world = world;
        registerAllSystems();
    }

    @Override
    public void update(double deltaTime) {
        for (Updatable sys : systems.values()) {
            sys.update(deltaTime);
        }
    }

    public <T extends Updatable> T get(Class<T> type) {
        Updatable system = systems.get(type);
        if (system == null) {
            throw new IllegalStateException("System " + type.getSimpleName() + " is not registered!");
        }
        return type.cast(system);
    }

    public void clear() {
        for (Updatable sys : systems.values()) {
            sys.clear();
        }
    }


    private void registerAllSystems() {
        register(new PaddleSystem(world.getPaddles()));
        register(new BallSystem(world.getBalls()));
        register(new PowerUpSystem(world.getBall(), world.getPaddle(), world.getPowerUps()));

        BrickSystem brickSystem = new BrickSystem(world.getBrickss(), this);
        // Set callback để cộng điểm khi brick bị phá hủy
        brickSystem.setOnBrickDestroyed(brick -> {
            world.addScore(brick.getScoreValue());
        });
        register(brickSystem);

        register(new MovedWallSystem(world.getMovedWallFactory()));
        register(new PortalSystem(world.getPortalFactory()));
        register(new CollisionSystem(world, this));
    }


    private <T extends Updatable> void register(T system) {
        systems.put(system.getClass(), system);
    }
}
