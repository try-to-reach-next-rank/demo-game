package com.example.demo.controller.system;

import com.example.demo.engine.GameWorld;
import com.example.demo.engine.Updatable;

import java.util.*;

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
        return type.cast(systems.get(type));
    }

    public void clear() {
        for (Updatable sys : systems.values()) {
            sys.clear();
        }
    }

    public boolean has(Class<? extends Updatable> type) {
        return systems.containsKey(type);
    }

    private void registerAllSystems() {
        register(new BallSystem(world.getBalls()));
        register(new BrickSystem(world.getBricks(), world.getPowerUps()));
        register(new PaddleSystem(world.getPaddle()));
        register(new PowerUpSystem(world.getBall(), world.getPaddle(), world.getPowerUps()));
    }

    private <T extends Updatable> void register(T system) {
        systems.put(system.getClass(), system);
    }
}
