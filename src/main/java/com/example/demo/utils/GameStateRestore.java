package com.example.demo.utils;

import com.example.demo.engine.GameWorld;
import com.example.demo.model.core.entities.bricks.Brick;
import com.example.demo.model.core.entities.PowerUp;
import com.example.demo.model.core.entities.ThePool;
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

        Brick[] restoredBricks = new Brick[state.getBricksData().size()];
        for (BrickData data : state.getBricksData()) {
            restoredBricks[data.getId()] = BrickFactoryUtil.createBrickFromData(data);
        }
        world.setBricks(restoredBricks);

        world.getPowerUps().clear();
        for (PowerUpData data : state.getPowerUpsData()) {
            PowerUp p = ThePool.PowerUpPool.acquire(data.getType());
            p.setPosition(data.getX(), data.getY());
            p.setVisible(data.isVisible());
            world.getPowerUps().add(p);
        }

        // TODO: FIX
        // if (world.getPowerUpSystem() != null) {
        //     PowerUpSystem system = world.getPowerUpSystem();
        //     system.reset();
        //     for (ActivePowerUpData d : state.getActivePowerUpsData()) {
        //         system.activateFromSave(d);
        //     }
        // }
    }
}
