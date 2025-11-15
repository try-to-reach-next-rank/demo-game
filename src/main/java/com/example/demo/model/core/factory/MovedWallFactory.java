package com.example.demo.model.core.factory;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.core.entities.MovedWall;
import com.example.demo.utils.GameRandom;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class MovedWallFactory {
    private final List<MovedWall> walls;

    public MovedWallFactory() {
        walls = new ArrayList<>();
    }

    public void createRandom(String imageKey) {
        MovedWall temp = new MovedWall(imageKey);
        double w = temp.getWidth();
        double h = temp.getHeight();

        // Random x, y, default duration
        int x = GameRandom.nextInt(GameVar.MAP_MIN_X + (int)w, GameVar.MAP_MAX_X - (int)w);
        int y = GameRandom.nextInt(GameVar.MAP_CENTER_Y - (int)h, GameVar.MAP_MAX_Y - (int)h - 50);
        double lifeTime = 10.0; // default 10 seconds

        create(imageKey, x, y, lifeTime);
    }

    public void create(String imageKey, int x, int y, double lifeTime) {
        MovedWall mw = new MovedWall(imageKey);

        // Random velocity
        int dir = GameRandom.nextBoolean() ? 1 : -1;
        mw.setDirection(new Vector2D(dir, 0));

        mw.activate(x, y, lifeTime);

        System.out.println("CREATED MOVED WALL: x = " + mw.getX() + " y = " + mw.getY() + " W = " + mw.getWidth() + " H = " + mw.getHeight());

        walls.add(mw);
    }

    public List<MovedWall> getMovedWalls() {
        return walls;
    }
}
