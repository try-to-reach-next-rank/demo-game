package com.example.demo.controller.system;

import com.example.demo.engine.Updatable;
import com.example.demo.model.core.entities.Ball;
import com.example.demo.model.core.entities.MovedWall;
import com.example.demo.model.core.factory.MovedWallFactory;
import com.example.demo.model.core.gameobjects.GameObject;
import com.example.demo.utils.Timer;
import com.example.demo.utils.Vector2D;
import com.example.demo.utils.var.GameVar;

public class MovedWallSystem implements Updatable {
    private final Timer spawnTimer;
    private final MovedWallFactory factory;
    private final double SPAWNINTERVAL = 10.0; // 5 seconds

    public MovedWallSystem(MovedWallFactory factory) {
        this.spawnTimer = new Timer();
        this.factory = factory;
        this.spawnTimer.start(SPAWNINTERVAL); // start the first 10-sec timer
    }

    @Override
    public void update(double deltaTime) {
        // 1. Spawn mới sau mỗi 10s
//        spawnTimer.update(deltaTime);
//        if (spawnTimer.isFinished()) {
//            factory.createRandom("wall_top");
//            spawnTimer.reset();
//            spawnTimer.start(SPAWNINTERVAL);
//        }
//
//        // 2. Update walls
//        var walls = factory.getMovedWalls();
//
//        // Dùng iterator để remove an toàn
//        walls.removeIf(w -> {
//            w.updateLifetime(deltaTime);       // 1. kiểm tra sống/chết
//
//            if (!w.isActive())
//                return true;                  // chết -> remove ngay
//
//            updateMovement(w, deltaTime);      // 2. còn sống -> update movement
//
//            return false;                      // giữ lại
//        });
    }

    @Override
    public void clear() {}

    private void updateMovement(MovedWall w, double dt) {
        Vector2D movement = w.getDirection().multiply(w.getSpeed() * dt);

        double newX = w.getX() + movement.x;
        double newY = w.getY() + movement.y;

        Vector2D dir = w.getDirection();

        // Horizontal bounds
        if (newX < GameVar.MAP_MIN_X) {
            newX = GameVar.MAP_MIN_X;
            w.setDirection(new Vector2D(-dir.x, dir.y)); // flip X
        }
        else if (newX + w.getWidth() > GameVar.MAP_MAX_X) {
            newX = GameVar.MAP_MAX_X - w.getWidth();
            w.setDirection(new Vector2D(-dir.x, dir.y));
        }

        // Vertical bounds (nếu muốn)
        if (newY < GameVar.MAP_MIN_Y) {
            newY = GameVar.MAP_MIN_Y;
            w.setDirection(new Vector2D(dir.x, -dir.y));
        }
        else if (newY + w.getHeight() > GameVar.MAP_MAX_Y) {
            newY = GameVar.MAP_MAX_Y - w.getHeight();
            w.setDirection(new Vector2D(dir.x, -dir.y));
        }

        w.setPosition(newX, newY);
    }
}
