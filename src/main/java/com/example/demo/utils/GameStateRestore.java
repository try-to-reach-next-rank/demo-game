package com.example.demo.utils;

import com.example.demo.controller.system.PowerUpSystem;
import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.Brick;
import com.example.demo.model.core.PowerUp;
import com.example.demo.model.core.ThePool;
import com.example.demo.model.state.ActivePowerUpData;
import com.example.demo.model.state.BrickData;
import com.example.demo.model.state.GameState;
import com.example.demo.model.state.PowerUpData;

public class GameStateRestore {
    private final ThePool pool;

    public GameStateRestore(ThePool pool) {
        this.pool = pool;
    }

    public void apply(GameState state, GameWorld world) {
        world.setCurrentLevel(state.getCurrentLevel());
        Sound.getInstance().playMusic(state.getCurrentTrackName(), state.getCurrentTrackTime());

        world.getPaddle().applyState(state.getPaddleData());
        world.getBall().applyState(state.getBallData());

        Brick[] bricks = world.getBricks();
        for (BrickData data : state.getBricksData()) {
            if (data.getId() >= 0 && data.getId() < bricks.length) {
                bricks[data.getId()].applyState(data);
            }
        }

        world.getPowerUps().clear();
        for (PowerUpData data : state.getPowerUpsData()) {
            PowerUp p = ThePool.PowerUpPool.acquire(data.getType());
            p.setPosition(data.getX(), data.getY());
            p.setVisible(data.isVisible());
            world.getPowerUps().add(p);
        }

        if (world.getPowerUpSystem() != null) {
            PowerUpSystem system = world.getPowerUpSystem();
            system.reset();
            for (ActivePowerUpData d : state.getActivePowerUpsData()) {
                system.activateFromSave(d);
            }
        }
    }
}
